package servlets.sn;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.exceptions.AccessControlException;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import impl.access_control.AccessControllerClass;
import impl.access_control.PageResource;
import impl.authenticator.AuthenticatorClass;
import sn.PostObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class DeletePostServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(req, resp);

            Capability capability = (Capability) req.getSession().getAttribute("capability");

            String postIdString = req.getParameter("post_id");
            int postId = Integer.valueOf(postIdString);

            SN sn = new SN();

            PostObject post = sn.getPost(postId);
            int pageId = post.getPageId();

            sn.disconnect();

            capability = accessController.checkPermission(capability, new PageResource(pageId), Operation.DELETE_POST);
            req.getSession().setAttribute("capability", capability);

            sn = new SN();

            sn.deletePost(post);

            sn.disconnect();

            resp.sendRedirect("/myApp/page?page_id=" + pageId);

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (AccessControlException e) {
            resp.sendRedirect("/myApp/error_pages/access_control_error.html");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
