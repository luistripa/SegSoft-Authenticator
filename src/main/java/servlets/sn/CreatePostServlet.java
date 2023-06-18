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
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class CreatePostServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(req, resp);

            String pageIdString = req.getParameter("page_id");
            int pageId = Integer.parseInt(pageIdString);

            Capability capability = (Capability) req.getSession().getAttribute("capability");

            accessController.checkPermission(capability, new PageResource(pageId), Operation.CREATE_POST);

            URL resource = getClass().getResource("/templates/sn/create-new-post.pug");

            String render = Pug4J.render(resource, null);

            resp.getWriter().println(render);

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (AccessControlException e) {
            resp.sendRedirect("/myApp/error_pages/access_control_error.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(req, resp);

            String pageIdString = req.getParameter("page_id");
            int pageId = Integer.parseInt(pageIdString);

            Capability capability = (Capability) req.getSession().getAttribute("capability");

            accessController.checkPermission(capability, new PageResource(pageId), Operation.CREATE_POST);

            String text = req.getParameter("post-text");

            SN sn = new SN();
            sn.newPost(pageId, LocalDate.now().toString(), text);

            resp.sendRedirect("/myApp/page?page_id=" + pageId);

            sn.disconnect();

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
