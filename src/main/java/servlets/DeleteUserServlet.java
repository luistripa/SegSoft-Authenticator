package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.*;
import de.neuland.pug4j.Pug4J;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class DeleteUserServlet extends HttpServlet {

    private static final Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        URL resource = getClass().getResource("/templates/delete-user.pug");

        String render = Pug4J.render(resource, null);

        res.getWriter().println(render);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");

        try {
            Account account = authenticator.check_authenticated_request(req, res);

            if (account.getName().equals("root")) {
                authenticator.delete_account(username);
                res.sendRedirect("/myApp/success_pages/delete_user_success.html");

            } else {
                res.sendRedirect("/myApp/error_pages/root_only_error.html");
            }

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (AccountUnlockedException e) {
            res.sendRedirect("/myApp/error_pages/account_unlocked_error.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");
        }
    }
}
