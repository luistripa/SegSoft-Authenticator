package servlets.sn;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.exceptions.AccessControlException;
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

public class DeletePageServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(req, resp);

            URL resource = getClass().getResource("/templates/sn/delete-page.pug");

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

            String pageIdString = req.getParameter("page_id");
            int pageId = Integer.parseInt(pageIdString);

            capability = accessController.checkPermission(capability, new PageResource(pageId), Operation.DELETE_PAGE);
            req.getSession().setAttribute("capability", capability);

            SN sn = new SN();

            PageObject pageObject = sn.getPage(pageId);
            sn.deletePage(pageObject);

            sn.disconnect();

            accessController.generateNewCode();

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
