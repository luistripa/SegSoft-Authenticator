package impl;

import org.apache.ibatis.jdbc.ScriptRunner;
import sn.SN;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Initialization {

    private static final String CONNECTION_STRING = "jdbc:sqlite:database.db";

    private static Initialization INSTANCE;

    public static void initialize() {
        if (INSTANCE == null) {
            INSTANCE = new Initialization();
        }
    }

    private Initialization() {
        try {
            Class.forName("org.sqlite.JDBC");

            initializeSocialNetwork();
            initializeAuthenticatorAndAccessControl();
            populateDB();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeAuthenticatorAndAccessControl() {
        try (InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/database/create-db.sql"))) {
            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
                ScriptRunner sr = new ScriptRunner(conn);

                sr.runScript(in);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeSocialNetwork() {
        try {
            SN sn = new SN();
            sn.DBDrop();
            sn.DBBuild();
            sn.disconnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateDB() {
        try (InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/database/populate.sql"))) {
            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
                ScriptRunner sr = new ScriptRunner(conn);

                sr.runScript(in);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
