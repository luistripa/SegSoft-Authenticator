package servlets;

import api.access_control.AccessController;
import api.access_control.Role;
import api.access_control.exceptions.RoleNotFoundException;
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
import java.sql.SQLException;
import java.util.Optional;

public class SetRoleServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            if(!account.getName().equals("root"))
                throw new AuthenticationException();

            URL resource = getClass().getResource("/templates/set-role.pug");

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

            if(!account.getName().equals("root"))
                throw new AuthenticationException();

            String username = req.getParameter("username");
            String roleId = req.getParameter("role");

            Account acc = authenticator.get_account(username);

            if(acc == null)
                throw new UndefinedAccountException();

            accessController.setRole(acc, new Role(roleId));

            resp.sendRedirect("/myApp/success_pages/set_role_success.html");

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (RoleNotFoundException e) {
            resp.sendRedirect("/myApp/error_pages/role_not_found_error.html");
        }
    }
}
