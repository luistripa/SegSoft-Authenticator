package servlets;

import api.authenticator.Account;
import api.authenticator.Authenticator;
import api.authenticator.exceptions.AccountUnlockedException;
import api.authenticator.exceptions.AuthenticationException;
import api.authenticator.exceptions.UndefinedAccountException;
import impl.authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UnlockAccountServlet extends HttpServlet {

    private final String webPage = """
            <html>
                <head>
                    <title>Unlock Account</title>
                </head>
                <body>
                    <h1>Unlock Account</h1>
                    <form action="unlock-account" method="POST">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                        <br>
                        <input type="submit" value="Unlock Account">
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
                authenticator.unlock_account(username);

                res.sendRedirect("/myApp/success_pages/unlock_account_success.html");

            } else {
                res.sendRedirect("/myApp/error_pages/root_only_error.html");
            }

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");

        } catch (AccountUnlockedException e) {
            res.sendRedirect("/myApp/error_pages/account_unlocked_error.html");
        }
    }


}
