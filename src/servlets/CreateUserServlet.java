package servlets;

import api.Account;
import api.Authenticator;
import api.exceptions.*;
import impl.AuthenticatorClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateUserServlet extends HttpServlet {

    private final String webPage = """
            <html>
                <head>
                    <title>Login</title>
                </head>
                <body>
                    <h1>Create User</h1>
                    <form action="create-user" method="POST">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                        <label for="confpassword">Confirm Password</label>
                        <input type="password" id="confpassword" name="confpassword" required>
                        <input type="submit" value="Create User">
                    </form>
                </body>
            </html> 
            """;

    private final Authenticator authenticator = new AuthenticatorClass();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(webPage);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confpassword = request.getParameter("confpassword");

        try {
            // TODO: Create user should only work for "root" user

            authenticator.create_account(username, password, confpassword);

            response.sendRedirect("ManageUsers");

        } catch (AuthenticationException | DifferentPasswordsException | AccountAlreadyExistsException e) {
            response.sendRedirect("create-user");
        }
    }
}
