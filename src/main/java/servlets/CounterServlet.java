package servlets;

import api.Authenticator;
import api.exceptions.AuthenticationException;
import api.exceptions.UndefinedAccountException;
import impl.AuthenticatorClass;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class CounterServlet extends HttpServlet {

    private static final Authenticator authenticator = AuthenticatorClass.getInstance();

    static int counter = 0;

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        try {
            authenticator.check_authenticated_request(req, res);

            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("</HEAD>");
            out.println("<BODY>");
            out.println("<H1>The Counter App!</H1>");
            out.println("<H1>Value="+counter+"</H1>");
            out.print("<form action=\"");
            out.print("counter\" ");
            out.println("method=GET>");
            out.println("<br>");
            out.println("<input type=submit name=increment>");
            out.println("</form>");
            out.println("</BODY>");

            out.println("</HTML>");
            counter++;

        } catch (AuthenticationException e) {
            res.sendRedirect("/myApp/error_pages/authentication_error.html");

        } catch (UndefinedAccountException e) {
            res.sendRedirect("/myApp/error_pages/undefined_account_error.html");
        }
    }
}

