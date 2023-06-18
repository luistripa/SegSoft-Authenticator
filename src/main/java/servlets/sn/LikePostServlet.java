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
import sn.PageObject;
import sn.PostObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class LikePostServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            authenticator.check_authenticated_request(req, resp);

            Capability capability = (Capability) req.getSession().getAttribute("capability");

            String pageIdString = req.getParameter("post_id");
            int postId = Integer.parseInt(pageIdString);

            SN sn = new SN();

            PostObject post = sn.getPost(postId);
            int pageId = post.getPageId();

            capability = accessController.checkPermission(capability, new PageResource(pageId), Operation.LIKE_UNLIKE_POST);
            req.getSession().setAttribute("capability", capability);

            // Get the page of the user that is liking/unliking the post
            PageObject userPage = null;
            List<PageObject> allPages = sn.getAllPages();
            for (PageObject page : allPages) {
                if (page.getUserId().equals(capability.getAccount().getName())) {
                    userPage = page;
                    break;
                }
            }

            if (userPage == null) {
                throw new UndefinedAccountException();
            }

            List<PageObject> likes = sn.getLikes(postId);

            // Check if the user has already liked the post
            boolean liked = false;
            for (PageObject like : likes) {
                if (like.getUserId().equals(capability.getAccount().getName())) {
                    liked = true;
                    break;
                }
            }

            if (liked) {
                sn.unlike(postId, userPage.getPageId());

            } else {
                sn.like(postId, userPage.getPageId());
            }

            sn.disconnect();

            resp.sendRedirect("/myApp/post?post_id=" + postId);

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
