package impl;

import api.Account;
import api.Authenticator;
import api.DBService;
import api.exceptions.AuthenticationException;
import api.exceptions.LockedAccountException;
import api.exceptions.UndefinedAccountException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticatorClass implements Authenticator {

    private final DBService dbService;

    public AuthenticatorClass() {
        dbService = DBServiceClass.getInstance();
    }

    @Override
    public void create_account(String name, String pwd1, String pwd2) {
        // TODO: Implement authenticator create_account
    }

    @Override
    public void delete_account(String name) {
        // TODO: Implement authenticator delete_account
    }

    @Override
    public Account get_account(String name) {
        // TODO: Implement authenticator get_account
        return null;
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) {
        // TODO: Implement authenticator change_pwd
    }

    @Override
    public Account login(String name, String pwd) throws UndefinedAccountException, LockedAccountException, AuthenticationException {
        // TODO: Implement authenticator login
        return null;
    }

    @Override
    public Account check_authenticated_request(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {
        // TODO: Implement authenticator check_authenticated_request
        return null;
    }

    @Override
    public void logout(Account acc) {
        // TODO: Implement authenticator logout
    }
}
