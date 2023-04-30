package servlets;

import api.Account;
import api.Authenticator;
import api.exceptions.*;
import impl.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LockAccountServlet extends HttpServlet {

    private final String webPage = """
            <html>
                <head>
                    <title>Lock Account</title>
                </head>
                <body>
                    <h1>Lock Account</h1>
                    <form action="lock-account" method="POST">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                        <br>
                        <input type="submit" value="Lock Account">
                    </form>
                </body>
            </html> 
            """;

    private final Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println(webPage);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            Account account = authenticator.check_authenticated_request(req, res);

            String username = req.getParameter("username");

            if (account.getName().equals("root")) {
                authenticator.lock_account(username);

                res.sendRedirect("/myApp/success_pages/lock_account_success.html");

            } else {
                res.sendRedirect("/myApp/error_pages/root_only_error.html");
            }

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (AccountLockedException e) {
            res.sendRedirect("/myApp/error_pages/account_locked_error.html");
        }
    }

}
