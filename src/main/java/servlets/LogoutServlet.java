package servlets;

import api.Account;
import api.Authenticator;
import api.exceptions.AccountLockedException;
import api.exceptions.AuthenticationException;
import api.exceptions.UndefinedAccountException;
import impl.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class LogoutServlet extends HttpServlet {

    private String webPage = """
            <html>
                <head>
                    <title>Logout</title>
                </head>
                <body>
                    <h1>Logout</h1>
                    <form action="logout" method="POST">
                        <input type="submit" value="Logout">
                    </form>
                </body>
            </html>
            """;

    private final Authenticator authenticator = AuthenticatorClass.getInstance();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        out.println(webPage);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {
            Account account = authenticator.check_authenticated_request(req, res);
            authenticator.logout(account);
            
            // Remove session token
            HttpSession session = req.getSession();
            session.removeAttribute("token");

            res.sendRedirect("/myApp/success_pages/logged_out_success.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }

}
