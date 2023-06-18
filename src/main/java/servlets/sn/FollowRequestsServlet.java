package servlets.sn;

import api.access_control.AccessController;
import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.exceptions.AccessControlException;
import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
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
import java.sql.SQLException;

public class FollowRequestsServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();
    AccessController accessController = AccessControllerClass.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, resp);

            String pageIdString = req.getParameter("page_id");
            int pageId = Integer.parseInt(pageIdString);
            String action = req.getParameter("action"); // request or unfollow

            Capability capability = (Capability) req.getSession().getAttribute("capability");
            capability = accessController.checkPermission(capability, new PageResource(pageId), Operation.SUBMIT_FOLLOW_REQUEST);
            req.getSession().setAttribute("capability", capability);

            SN sn = new SN();

            PageObject followerPage = sn.getPageByUserId(account.getName());

            if (followerPage.getPageId() == pageId) {
                throw new AccessControlException();
            }

            if (action.equals("request")) {
                sn.follows(followerPage.getPageId(), pageId, FState.PENDING);
                sn.disconnect();

            } else if (action.equals("unfollow")) {
                sn.unfollows(followerPage.getPageId(), pageId);
                sn.disconnect();

                accessController.revokePermission(account, new PageResource(pageId), Operation.READ_POST);
                accessController.revokePermission(account, new PageResource(pageId), Operation.LIKE_UNLIKE_POST);

                accessController.generateNewCode();
            }

            resp.sendRedirect("/myApp/page?page_id=" + pageId);

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (AccessControlException e) {
            resp.sendRedirect("/myApp/error_pages/access_control_error.html");

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
