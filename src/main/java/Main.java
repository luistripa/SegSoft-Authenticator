
import api.access_control.exceptions.AccessControlException;
import api.access_control.exceptions.OperationNotFoundException;
import api.access_control.exceptions.RoleNotFoundException;
import api.authenticator.exceptions.UndefinedAccountException;
import de.neuland.pug4j.Pug4J;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws SQLException, IOException, OperationNotFoundException, RoleNotFoundException, AccessControlException, InterruptedException, UndefinedAccountException {
        Map<String, Object> model = Map.of(
                "logged_in", false,
                "username", "root"
        );

        String render = Pug4J.render("src/main/resources/templates/logout.pug", model);

        // Write to file
        Files.write(Paths.get("./index.html"), render.getBytes());

    }
}
