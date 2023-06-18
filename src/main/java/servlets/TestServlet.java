package servlets;

import de.neuland.pug4j.Pug4J;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class TestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        URL resource = getClass().getResource("/templates/template.pug");
        if(resource == null) {
            throw new RuntimeException("asdasd");
        }

        Map<String, Object> model = Map.of(
                "posts", List.of(
                        Map.of("title", "Post 1", "body", "Body 1"),
                        Map.of("title", "Post 2", "body", "Body 2"),
                        Map.of("title", "Post 3", "body", "Body 3")
                )
        );

        String render = Pug4J.render(resource, model);
        response.getWriter().println(render);
    }

}
