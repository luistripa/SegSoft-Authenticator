package servlets;

import api.Authenticator;
import api.DBService;
import impl.AuthenticatorClass;
import impl.DBServiceClass;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class TestingServlet extends HttpServlet {

    private static final DBService dbService = DBServiceClass.getInstance();

    static int counter = 0;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<H1>The Counter App!</H1>");
        out.println("<H1>Value="+counter+"</H1>");
            out.print("<form action=\"");
            out.print("Test\" ");
            out.println("method=GET>");
            out.println("<br>");
            out.println("<input type=submit name=increment>");
            out.println("</form>");
        out.println("</BODY>");

        out.println("</HTML>");
        counter++;
    }
}

