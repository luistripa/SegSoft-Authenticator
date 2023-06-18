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
import sn.PostObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class PostDetailServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticator.check_authenticated_request(req, resp);

            String postIdString = req.getParameter("post_id");
            int postId = Integer.valueOf(postIdString);

            Capability capability = (Capability) req.getSession().getAttribute("capability");

            SN sn = new SN();
            PostObject post = sn.getPost(postId);

            int pageId = post.getPageId();
            PageObject page = sn.getPage(pageId);

            // TODO: Check if user has already liked this post

            accessController.checkPermission(capability, new PageResource(pageId), Operation.READ_POST);

            URL resource = getClass().getResource("/templates/sn/post.pug");

            Map<String, Object> model = Map.of(
                    "id", postId,
                    "author", page.getUserId(),
                    "page_id", post.getPageId(),
                    "text", post.getPostText(),
                    "date", post.getPostDate()
            );

            String render = Pug4J.render(resource, model);

            sn.disconnect();

            resp.getWriter().println(render);

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
