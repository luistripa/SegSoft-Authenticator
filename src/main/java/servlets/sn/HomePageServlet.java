package servlets.sn;

import api.authenticator.Authenticator;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.authenticator.AuthenticatorClass;
import sn.PageObject;
import sn.SN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePageServlet extends HttpServlet {

    Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL resource = getClass().getResource("/templates/sn/home.pug");

        try {
            authenticator.check_authenticated_request(req, resp);

            SN sn = new SN();

            List<PageObject> allPages = sn.getAllPages();

            List<Map<String, Object>> allPagesMap = new ArrayList<>(allPages.size());

            allPages.forEach(pageObject -> {
                allPagesMap.add(
                        Map.of(
                                "id", pageObject.getPageId(),
                                "user_id", pageObject.getUserId(),
                                "title", pageObject.getPageTitle()
                        )
                );
            });

            Map<String, Object> model = Map.of(
                    "pages", allPagesMap
            );

            String render = Pug4J.render(resource, model);

            resp.getWriter().write(render);

            sn.disconnect();

        } catch (AuthenticationException e) {
            resp.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (UndefinedAccountException e) {
            resp.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
