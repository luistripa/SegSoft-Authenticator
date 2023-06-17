package api;

import api.authenticator.Account;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthenticatorDBService {

    /**
     * Creates a new account in the database.
     *
     * @param name The account name
     * @param hash The password hash
     * @param is_locked Whether the account is locked or not
     * @param logged_in Whether the account is logged in or not
     * @throws SQLException If an error occurs while creating the account
     */
    void createAccount(String name, String hash, boolean is_locked, boolean logged_in) throws SQLException;

    /**
     * Deletes an existing account from the database.
     *
     * @param name The account name
     * @throws SQLException If an error occurs while deleting the account
     */
    void deleteAccount(String name) throws SQLException;

    /**
     * Changes the password of an existing account.
     *
     * @param name The account name
     * @param newHash The new password hash
     * @throws SQLException If an error occurs while changing the password
     */
    void changePassword(String name, String newHash) throws SQLException;

    void lockAccount(String name) throws SQLException;

    void unlockAccount(String name) throws SQLException;

    /**
     * Gets an account with the given name from the database.
     *
     * @param name The account name
     * @return The account object
     * @throws SQLException If an error occurs while retrieving the account
     */
    Optional<Account> getAccount(String name) throws SQLException;

    void login(String name) throws SQLException;

    void logout(String name) throws SQLException;
}
