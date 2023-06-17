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

public class CreateUserServlet extends HttpServlet {

    private final String webPage = """
            <html>
                <head>
                    <title>Create User</title>
                </head>
                <body>
                    <h1>Create User</h1>
                    <form action="create-user" method="POST">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                        <br>
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                        <br>
                        <label for="confpassword">Confirm Password</label>
                        <input type="password" id="confpassword" name="confpassword" required>
                        <br>
                        <input type="submit" value="Create User">
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
            String password = req.getParameter("password");
            String confpassword = req.getParameter("confpassword");

            if (account.getName().equals("root")) {
                authenticator.create_account(username, password, confpassword);

                res.sendRedirect("/myApp/success_pages/create_user_success.html");

            } else {
                res.sendRedirect("/myApp/error_pages/root_only_error.html");
            }

        } catch (AccountAlreadyExistsException e) {
            res.sendRedirect("/myApp/error_pages/account_already_exists_error.html");

        } catch (DifferentPasswordsException e) {
            res.sendRedirect("/myApp/error_pages/different_passwords_error.html");

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }
}
