package servlets;

import api.access_control.AccessController;
import api.access_control.exceptions.RoleAlreadyExistsException;
import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.access_control.AccessControllerClass;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class NewRoleServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            if(!account.getName().equals("root")) {
                resp.sendRedirect("/myApp/error_pages/root_only_error.html");
                return;
            }

            URL resource = getClass().getResource("/templates/new-role.pug");

            String render = Pug4J.render(resource, null);

            resp.getWriter().println(render);

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            if(!account.getName().equals("root")) {
                resp.sendRedirect("/myApp/error_pages/root_only_error.html");
                return;
            }

            String roleId = req.getParameter("role");

            accessController.newRole(roleId);

            resp.sendRedirect("/myApp/manage-users");

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (RoleAlreadyExistsException e) {
            resp.sendRedirect("/myApp/error_pages/role_already_exists_error.html");
        }
    }
}
