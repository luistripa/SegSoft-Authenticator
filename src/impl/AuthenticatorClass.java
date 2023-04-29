package impl;

import api.Account;
import api.Authenticator;
import api.DBService;
import api.exceptions.*;
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

public class AuthenticatorClass implements Authenticator {

    private final DBService dbService;

    public AuthenticatorClass() {
        dbService = DBServiceClass.getInstance();
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
            DBServiceClass.getInstance().createAccount(name, hash, false, false);

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void delete_account(String name) throws AuthenticatorException, AccountUnlockedException {
        try {
            Optional<Account> acc = DBServiceClass.getInstance().getAccount(name);

            if (acc.isPresent()) {
                Account account = acc.get();

                if(!account.isLocked()) {
                    throw new AccountUnlockedException();
                }

                DBServiceClass.getInstance().deleteAccount(name);
            }

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public Optional<Account> get_account(String name) throws AuthenticatorException {
        try {
            return DBServiceClass.getInstance().getAccount(name);

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) throws DifferentPasswordsException, AuthenticatorException {
        if(!pwd1.equals(pwd2)) {
            throw new DifferentPasswordsException();
        }

        // TODO: Consider checking if account exists

        try {
            String hash = Hashing.sha256().hashString(pwd1, StandardCharsets.UTF_8).toString();
            DBServiceClass.getInstance().changePassword(name, hash);

        } catch (SQLException e) {
            throw new AuthenticatorException(e);
        }
    }

    @Override
    public Account login(String name, String pwd) throws UndefinedAccountException, AccountLockedException,
            AuthenticationException, AuthenticatorException {

        try {
            Optional<Account> acc = DBServiceClass.getInstance().getAccount(name);

            if (acc.isPresent()) {
                Account account = acc.get();

                if(account.isLocked()) {
                    throw new AccountLockedException();
                }

                String hash = Hashing.sha256().hashString(pwd, StandardCharsets.UTF_8).toString();
                if(!hash.equals(account.getHash())) {
                    throw new AuthenticationException();
                }

                DBServiceClass.getInstance().login(name);
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

            Optional<Account> acc = DBServiceClass.getInstance().getAccount(username);

            if(acc.isPresent()) {
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
            DBServiceClass.getInstance().logout(name);

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
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new AuthenticatorException(e);
        }
    }
}
