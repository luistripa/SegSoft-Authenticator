package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.AuthenticatorDBService;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;
import impl.authenticator.AuthenticatorClass;
import impl.authenticator.AuthenticatorDBServiceClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

public class ManageUsersServlet extends HttpServlet {

    private static final AuthenticatorDBService dbService = AuthenticatorDBServiceClass.getInstance();

    private static final Authenticator authenticator = AuthenticatorClass.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        URL resource = getClass().getResource("/templates/manage-users.pug");

        Account account = null;
        try {
            account = authenticator.check_authenticated_request(request, response);

        } catch (AuthenticationException | UndefinedAccountException ignored) {}

        Map<String, Object> model = Map.of(
                "logged_in", account != null,
                "username", account != null ? account.getName() : "",
                "role", account != null ? account.getRole().roleId() : ""
        );

        String render = Pug4J.render(resource, model);

        response.getWriter().println(render);
    }

}
