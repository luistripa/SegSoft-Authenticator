package servlets;

import api.Account;
import api.Authenticator;
import api.DBService;
import api.exceptions.AccountLockedException;
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

public class LoginServlet extends HttpServlet {

    private static DBService dbService = DBServiceClass.getInstance();

    private String webPage = """
            <html>
                <head>
                    <title>Login</title>
                </head>
                <body>
                    <h1>Login</h1>
                    <form action="login" method="POST">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                        <input type="submit" value="Login">
                    </form>
                </body>
            </html>
            """;

    private final Authenticator authenticator = AuthenticatorClass.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(webPage);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Account account = authenticator.login(username, password);

            HttpSession session = request.getSession();

            String token = authenticator.generateToken(account);
            session.setAttribute("token", token);

            response.sendRedirect("manage-users");

        } catch (UndefinedAccountException | AuthenticationException | AccountLockedException e) {
            response.sendRedirect("login");
        }
    }
}
