package servlets.sn;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.Role;
import api.access_control.exceptions.AccessControlException;
import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.access_control.AccessControllerClass;
import impl.access_control.PageResource;
import impl.authenticator.AuthenticatorClass;
import sn.PageObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class CreatePageServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(req, resp);

            URL resource = getClass().getResource("/templates/sn/create-new-page.pug");

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
            authenticator.check_authenticated_request(req, resp);

            Capability capability = (Capability) req.getSession().getAttribute("capability");

            accessController.checkPermission(capability, new PageResource(-1), Operation.CREATE_PAGE);

            String pageTitle = req.getParameter("page_title");
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String picture = "page.png";

            Account account = authenticator.get_account(username);

            if (account == null)
                throw new UndefinedAccountException();

            SN sn = new SN();

            PageObject pageObject = sn.newPage(username, email, pageTitle, picture);

            sn.disconnect();

            // Update role permissions
            accessController.grantPermission(new Role("Admins"), new PageResource(pageObject.getPageId()), Operation.READ_PAGE);
            accessController.grantPermission(new Role("Admins"), new PageResource(pageObject.getPageId()), Operation.SUBMIT_FOLLOW_REQUEST);
            accessController.grantPermission(new Role("Admins"), new PageResource(pageObject.getPageId()), Operation.DELETE_PAGE);
            accessController.grantPermission(new Role("Authors"), new PageResource(pageObject.getPageId()), Operation.READ_PAGE);
            accessController.grantPermission(new Role("Authors"), new PageResource(pageObject.getPageId()), Operation.SUBMIT_FOLLOW_REQUEST);

            // Update owner account permissions
            accessController.grantPermission(account, new PageResource(pageObject.getPageId()), Operation.CREATE_POST);
            accessController.grantPermission(account, new PageResource(pageObject.getPageId()), Operation.DELETE_POST);
            accessController.grantPermission(account, new PageResource(pageObject.getPageId()), Operation.APPROVE_FOLLOW_REQUEST);
            accessController.grantPermission(account, new PageResource(pageObject.getPageId()), Operation.READ_POST);
            accessController.grantPermission(account, new PageResource(pageObject.getPageId()), Operation.LIKE_UNLIKE_POST);

            resp.sendRedirect("/myApp/manage-users");

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (AccessControlException e) {
            resp.sendRedirect("/myApp/error_pages/access_control_error.html");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
