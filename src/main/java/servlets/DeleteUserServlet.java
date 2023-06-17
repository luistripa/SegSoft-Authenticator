package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.*;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteUserServlet extends HttpServlet {

    private final String webPage = """
            <html>
                <head>
                    <title>Delete User</title>
                </head>
                <body>
                    <h1>Delete User</h1>
                    <form action="delete-user" method="POST">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                        <br>
                        <input type="submit" value="Delete User">
                    </form>
                </body>
            </html>
            """;

    private static final Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        out.println(webPage);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");

        try {
            Account account = authenticator.check_authenticated_request(req, res);

            if (account.getName().equals("root")) {
                authenticator.delete_account(username);
                res.sendRedirect("/myApp/success_pages/delete_user_success.html");

            } else {
                res.sendRedirect("/myApp/error_pages/root_only_error.html");
            }

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (AccountUnlockedException e) {
            res.sendRedirect("/myApp/error_pages/account_unlocked_error.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");
        }
    }
}
