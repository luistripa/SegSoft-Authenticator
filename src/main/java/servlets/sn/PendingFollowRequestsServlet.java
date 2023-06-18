package servlets.sn;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.access_control.Operation;
import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.access_control.AccessControllerClass;
import impl.access_control.PageResource;
import impl.authenticator.AuthenticatorClass;
import sn.FState;
import sn.PageObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class PendingFollowRequestsServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            SN sn = new SN();

            PageObject userPage = sn.getPageByUserId(account.getName());

            List<PageObject> pendingRequests = sn.getPendingFollowers(userPage.getPageId());

            sn.disconnect();

            URL resource = getClass().getResource("/templates/sn/pending-follow-requests.pug");

            Map<String, Object> model = Map.of(
                    "requests", pendingRequests
            );

            String render = Pug4J.render(resource, model);

            resp.getWriter().println(render);


        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            String followerIdString = req.getParameter("follower_id");
            int followerId = Integer.parseInt(followerIdString);
            String action = req.getParameter("action"); // Accept or Reject

            SN sn = new SN();

            PageObject followeePage = sn.getPageByUserId(account.getName());

            sn.disconnect();

            Capability capability = (Capability) req.getSession().getAttribute("capability");
            capability = accessController.checkPermission(capability, new PageResource(followeePage.getPageId()), Operation.APPROVE_FOLLOW_REQUEST);
            req.getSession().setAttribute("capability", capability);

            sn = new SN();

            if (action.equals("accept"))
                sn.updatefollowsstatus(followerId, followeePage.getPageId(), FState.OK);

            else if (action.equals("reject"))
                sn.unfollows(followerId, followeePage.getPageId());

            PageObject followerPage = sn.getPage(followerId);

            sn.disconnect();

            if (action.equals("accept")) {
                Account followerAccount = authenticator.get_account(followerPage.getUserId());

                // Grant permission to the follower
                accessController.grantPermission(followerAccount, new PageResource(followeePage.getPageId()), Operation.READ_POST);
                accessController.grantPermission(followerAccount, new PageResource(followeePage.getPageId()), Operation.LIKE_UNLIKE_POST);

                accessController.generateNewCode();
            }

            resp.sendRedirect("/myApp/pending-follow-requests");

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
