package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AccountLockedException;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {

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

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        out.println(webPage);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            Account account = authenticator.login(username, password);

            HttpSession session = req.getSession();

            String token = authenticator.generateToken(account);
            session.setAttribute("token", token);

            res.sendRedirect("/myApp/success_pages/logged_in_success.html");

        } catch (AccountLockedException e) {
            res.sendRedirect("/myApp/error_pages/account_locked_error.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }
}
