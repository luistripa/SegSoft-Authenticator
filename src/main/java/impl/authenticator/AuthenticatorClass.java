package impl.authenticator;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.AuthenticatorDBService;
import api.authenticator.exceptions.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.google.common.hash.Hashing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthenticatorClass implements Authenticator {

    private final static String SECRET = "Super Secret String";
    private final static String ISSUER = "Authenticator";
    private final static int EXPIRATION_TIME = 60 * 30;

    private Logger logger = Logger.getLogger(AuthenticatorClass.class.getName());
    private final AuthenticatorDBService dbService;

    private static final AuthenticatorClass INSTANCE = new AuthenticatorClass();

    public static AuthenticatorClass getInstance() {
        return INSTANCE;
    }

    public AuthenticatorClass() {
        dbService = AuthenticatorDBServiceClass.getInstance();
    }

    @Override
    public void create_account(String name, String pwd1, String pwd2) throws AccountAlreadyExistsException, DifferentPasswordsException, AuthenticatorException {
        if(!pwd1.equals(pwd2)) {
            throw new DifferentPasswordsException();
        }

        Optional<Account> acc = this.get_account(name);

        if (acc.isPresent())
            throw new AccountAlreadyExistsException();

        String hash = Hashing.sha256().hashString(pwd1, StandardCharsets.UTF_8).toString();

        try {
            AuthenticatorDBServiceClass.getInstance().createAccount(name, hash, false, false);
            logger.info("Account created: " + name);

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void delete_account(String name) throws AuthenticatorException, AccountUnlockedException {
        try {
            Optional<Account> acc = AuthenticatorDBServiceClass.getInstance().getAccount(name);

            if (acc.isPresent()) {
                Account account = acc.get();

                if(!account.isLocked()) {
                    throw new AccountUnlockedException();
                }

                AuthenticatorDBServiceClass.getInstance().deleteAccount(name);
                logger.info("Account deleted: " + name);
            }

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public Optional<Account> get_account(String name) throws AuthenticatorException {
        try {
            Optional<Account> account = AuthenticatorDBServiceClass.getInstance().getAccount(name);

            logger.info("Account retrieved: " + name);
            return account;

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) throws DifferentPasswordsException, AuthenticatorException {
        if(!pwd1.equals(pwd2)) {
            throw new DifferentPasswordsException();
        }

        try {
            String hash = Hashing.sha256().hashString(pwd1, StandardCharsets.UTF_8).toString();
            AuthenticatorDBServiceClass.getInstance().changePassword(name, hash);
            logger.info("Password changed: " + name);

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void lock_account(String username) throws AuthenticatorException, AccountLockedException, UndefinedAccountException {
        try {
            Optional<Account> acc = get_account(username);

            if (acc.isPresent()) {
                Account account = acc.get();

                if (account.isLocked()) {
                    throw new AccountLockedException();
                }

                dbService.lockAccount(username);
                logger.info("Account locked: " + username);

            } else {
                throw new UndefinedAccountException();
            }

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void unlock_account(String username) throws AuthenticatorException, AccountUnlockedException, UndefinedAccountException {
        try {
            Optional<Account> acc = get_account(username);

            if (acc.isPresent()) {
                Account account = acc.get();

                if (!account.isLocked()) {
                    throw new AccountUnlockedException();
                }

                dbService.unlockAccount(username);
                logger.info("Account unlocked: " + username);

            } else {
                throw new UndefinedAccountException();
            }

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public Account login(String name, String pwd) throws UndefinedAccountException, AccountLockedException,
            AuthenticationException, AuthenticatorException {

        try {
            Optional<Account> acc = AuthenticatorDBServiceClass.getInstance().getAccount(name);

            if (acc.isPresent()) {
                Account account = acc.get();

                if(account.isLocked()) {
                    throw new AccountLockedException();
                }

                String hash = Hashing.sha256().hashString(pwd, StandardCharsets.UTF_8).toString();
                if(!hash.equals(account.getHash())) {
                    throw new AuthenticationException();
                }

                AuthenticatorDBServiceClass.getInstance().login(name);
                logger.info("Account logged in: " + name);
                return account;

            } else {
                throw new UndefinedAccountException();
            }

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public Account check_authenticated_request(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException, UndefinedAccountException, AuthenticatorException {
        HttpSession session = req.getSession();
        String token = (String) session.getAttribute("token");

        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .acceptExpiresAt(EXPIRATION_TIME)
                    .build();
            decodedJWT = verifier.verify(token);

            String username = decodedJWT.getSubject();

            Optional<Account> acc = AuthenticatorDBServiceClass.getInstance().getAccount(username);

            if(acc.isPresent()) {
                logger.info("Account authenticated: " + username);
                return acc.get();
            }

            throw new UndefinedAccountException();


        } catch (JWTVerificationException exception){
            throw new AuthenticationException();

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void logout(Account acc) throws AuthenticatorException {
        try {
            String name = acc.getName();
            AuthenticatorDBServiceClass.getInstance().logout(name);
            logger.info("Account logged out: " + name);

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public String generateToken(Account account) throws AuthenticatorException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(account.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * EXPIRATION_TIME))
                .sign(algorithm);

        } catch (JWTCreationException e){
            throw new AuthenticatorException(e);
        }
    }
}
