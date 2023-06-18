package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class LogoutServlet extends HttpServlet {

    private final Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        URL resource = getClass().getResource("/templates/logout.pug");

        String render = Pug4J.render(resource, null);

        res.getWriter().println(render);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {
            Account account = authenticator.check_authenticated_request(req, res);
            authenticator.logout(account);
            
            // Remove session token
            HttpSession session = req.getSession();
            session.removeAttribute("token");

            res.sendRedirect("/myApp/manage-users");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }

}
