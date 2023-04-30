package servlets;

import api.Account;
import api.Authenticator;
import api.DBService;
import api.exceptions.AuthenticationException;
import api.exceptions.UndefinedAccountException;
import impl.AuthenticatorClass;
import impl.DBServiceClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class ManageUsersServlet extends HttpServlet {

    private String webPage =
            """
            <html>
                <head>
                    <title>Manage Users</title>
                </head>
                <body>
                    <h1>Manage Users - ###</h1>
                    <a href="http://localhost:8080/myApp/counter">Counter</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/login">Login</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/logout">Logout</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/create-user">Create User</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/delete-user">Delete User</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/change-password">Change Password</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/lock-account">Lock Account</a>
                    <br/>
                    <a href="http://localhost:8080/myApp/unlock-account">Unlock Account</a>
                </body>
            </html>
            """;

    private static final DBService dbService = DBServiceClass.getInstance();

    private static final Authenticator authenticator = AuthenticatorClass.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        Account account = null;
        try {
            account = authenticator.check_authenticated_request(request, response);

        } catch (AuthenticationException | UndefinedAccountException ignored) {}

        String webPageRender;
        if (account == null)
            webPageRender = new String(webPage).replaceAll("###", "Not logged in");
        else
            webPageRender = new String(webPage).replaceAll("###", String.format("Logged in as %s", account.getName()));

        out.println(webPageRender);
    }

}
