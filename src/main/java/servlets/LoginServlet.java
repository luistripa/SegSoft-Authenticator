package servlets;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AccountLockedException;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.access_control.AccessControllerClass;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class LoginServlet extends HttpServlet {

    private final Authenticator authenticator = AuthenticatorClass.getInstance();
    private final AccessController accessController = AccessControllerClass.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        URL resource = getClass().getResource("/templates/login.pug");

        String render = Pug4J.render(resource, null);

        res.getWriter().println(render);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            Account account = authenticator.login(username, password);

            HttpSession session = req.getSession();

            String token = authenticator.generateToken(account);
            session.setAttribute("token", token);

            Capability capability = accessController.getCapability(account);
            session.setAttribute("capability", capability);

            res.sendRedirect("/myApp/manage-users");

        } catch (AccountLockedException e) {
            res.sendRedirect("/myApp/error_pages/account_locked_error.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }
}
