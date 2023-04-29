package servlets;

import api.DBService;
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
                    <a href="http://localhost:8080/SegSoft-Authenticator/login">Login</a>
                    <br/>
                    <a href="http://localhost:8080/SegSoft-Authenticator/logout">Logout</a>
                    <br/>
                    <a href="http://localhost:8080/SegSoft-Authenticator/create-user">Create User</a>
                    <br/>
                    <a href="http://localhost:8080/SegSoft-Authenticator/delete-user">Delete User</a>
                    <br/>
                    <a href="http://localhost:8080/SegSoft-Authenticator/change-password">Change Password</a>
                </body>
            </html>
            """;

    private static final DBService dbService = DBServiceClass.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        Object token = session.getAttribute("token");

        String webPageRender;
        if (token == null)
            webPageRender = new String(webPage).replaceAll("###", "Not logged in");
        else
            webPageRender = new String(webPage).replaceAll("###", "Logged in");

        out.println(webPageRender);
    }

}
