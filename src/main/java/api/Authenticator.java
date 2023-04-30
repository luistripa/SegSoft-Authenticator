package api;

import api.exceptions.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Optional;


public interface Authenticator {

    /**
     * Creates a new account object.
     *
     * Created account info should be stored (serialized) in a persistent storage (file, sql(ite) database)
     *
     * The account password must be stored in an encrypted form (non-invertible hash)
     *
     * @param name The account name
     * @param pwd1 The password (clear text)
     * @param pwd2 The password confirmation (plaintext) - must be equal to pwd1
     */
    void create_account(String name, String pwd1, String pwd2) throws AuthenticationException, DifferentPasswordsException, AccountAlreadyExistsException;

    /**
     * Deletes an existing account object.
     *
     * Deleted account should be deleted from the associated persistent storage.
     *
     * The account cannot be logged in.
     * The account must be locked (so no one will authenticate in the meantime)
     *
     * @param name The name of the account to delete
     */
    void delete_account(String name) throws AccountUnlockedException;

    /**
     * Returns a clone (readonly) from an existing account object.
     *
     * @param name The name of the account to return
     * @return Account object
     */
    Optional<Account> get_account(String name) throws SQLException;

    /**
     * Changes the password of the account.
     *
     * Preconditions:
     *  - Name must identify a created account
     *  - pwd1 == pwd2
     *
     * @param name The name of that identifies the account
     * @param pwd1 Password (plaintext)
     * @param pwd2 Password confirmation (plaintext)
     */
    void change_pwd(String name, String pwd1, String pwd2) throws DifferentPasswordsException, AuthenticatorException;

    /**
     * Locks an account.
     *
     * Locked accounts cannot be logged into.
     * Only locked accounts can be deleted.
     *
     * @param username The name of the account to lock
     */
    void lock_account(String username) throws AuthenticatorException, AccountLockedException, UndefinedAccountException;

    /**
     * Unlocks an account.
     *
     * Only an unlocked account can be logged into.
     *
     * @param username The username of the account to unlock
     */
    void unlock_account(String username) throws AuthenticatorException, AccountUnlockedException, UndefinedAccountException;

    /**
     * Authenticates the caller.
     *
     * Checks if name identifies an existing account.
     * Checks if account is locked.
     * Compares the encryption of pwd with the stored hash
     *
     * Must NOT let password flow anywhere else!
     *
     * @param name The name that identifies the account
     * @param pwd The password (plaintext)
     * @return The authenticated account if all checks pass.
     */
    Account login(String name, String pwd) throws UndefinedAccountException, AccountLockedException, AuthenticationException, AuthenticatorException;

    /**
     * Authenticate the caller on every servlet interaction.
     *
     * Caller is supposed to be already logged in.
     *
     * Extracts the name and the hashed credentials from req state.
     * Possible implementations:
     *  - Cookie (avoid)
     *  - Session parameters
     *  - Token based (JSON Web Tokens)
     *
     *  This method should call Authenticator.login(String, String) if token retrieval is successful.
     *
     * @throws AuthenticationException If authentication fails (password invalid)
     *
     * @see Authenticator#login(String, String)
     *
     * @param req The
     * @param resp
     * @return Account object if authentication is successful
     */
    Account check_authenticated_request(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException, UndefinedAccountException;

    /**
     * Generates a new JWT for the given Account object.
     *
     * @param acc The account object
     * @return A String with the JWT
     * @throws AuthenticatorException If the JWT generation fails
     */
    String generateToken(Account acc) throws AuthenticatorException;

    /**
     * Logs an account out (setting logged_in to false)
     *
     * @param acc The account to logout
     */
    void logout(Account acc) throws AuthenticatorException;
}
