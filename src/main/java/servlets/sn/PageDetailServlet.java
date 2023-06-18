package servlets.sn;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.exceptions.AccessControlException;
import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.access_control.AccessControllerClass;
import impl.access_control.PageResource;
import impl.authenticator.AuthenticatorClass;
import org.checkerframework.checker.units.qual.A;
import sn.PageObject;
import sn.PostObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageDetailServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            HttpSession session = req.getSession();
            Capability capability = (Capability) session.getAttribute("capability");

            String pageIdString = req.getParameter("page_id");
            int pageId = Integer.valueOf(pageIdString);

            accessController.checkPermission(capability, new PageResource(pageId), Operation.READ_PAGE);

            // Verifies if user can read posts
            boolean can_read_posts = false;
            try {
                accessController.checkPermission(capability, new PageResource(pageId), Operation.READ_POST);
                can_read_posts = true;
            } catch (Exception ignored) {}

            SN sn = new SN();

            // Get posts if user can read the page posts
            List<Map<String, Object>> posts = new ArrayList<>();
            if (can_read_posts) {
                List<PostObject> pagePosts = sn.getPagePosts(pageId);

                pagePosts.forEach(post -> {
                    posts.add(
                            Map.of(
                                    "id", post.getPostId(),
                                    "text", post.getPostText()
                            )
                    );
                });
            }

            PageObject page = sn.getPage(pageId);

            URL resource = getClass().getResource("/templates/sn/page-detail.pug");

            Map<String, Object> model = Map.of(
                    "page", Map.of(
                            "id", page.getPageId(),
                            "title", page.getPageTitle(),
                            "user_id", page.getUserId()
                    ),
                    "can_read_posts", can_read_posts,
                    "posts", posts
            );

            String render = Pug4J.render(resource, model);

            resp.getWriter().write(render);

            sn.disconnect();

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
