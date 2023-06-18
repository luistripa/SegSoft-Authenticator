package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.DifferentPasswordsException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class ChangePasswordServlet extends HttpServlet {

    private final Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(request, response);

            URL resource = getClass().getResource("/templates/change-password.pug");

            String render = Pug4J.render(resource, null);

            response.getWriter().println(render);

        } catch (AuthenticationException e) {
            response.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            response.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, res);

            String password = req.getParameter("password");
            String confpassword = req.getParameter("confpassword");

            authenticator.change_pwd(account.getName(), password, confpassword);
            res.sendRedirect("/myApp/success_pages/change_password_success.html");

        } catch (DifferentPasswordsException e) {
            res.sendRedirect("/myApp/error_pages/different_passwords_error.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }

}
