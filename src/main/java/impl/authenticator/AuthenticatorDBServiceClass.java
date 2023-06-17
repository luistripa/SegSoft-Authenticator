package impl.authenticator;

import api.access_control.Role;
import api.authenticator.Account;
import api.AuthenticatorDBService;
import impl.AccountClass;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Optional;

public class AuthenticatorDBServiceClass implements AuthenticatorDBService {

    public static final String DB_NAME = "database.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    private static AuthenticatorDBService instance;

    public static AuthenticatorDBService getInstance() {
        if (instance == null) {
            instance = new AuthenticatorDBServiceClass();
        }
        return instance;
    }

    private AuthenticatorDBServiceClass() {
        try {
            Class.forName("org.sqlite.JDBC");
            initDB();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDB() {
        try (InputStream in = getClass().getResourceAsStream("/database/create-db.sql")) {
            if (in == null) {
                throw new RuntimeException("Could not find create-db.sql");
            }

            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
                ScriptRunner sr = new ScriptRunner(conn);

                sr.runScript(new InputStreamReader(in));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAccount(String name, String hash, boolean is_locked, boolean logged_in) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO accounts (username, password_hash, is_locked, logged_in) VALUES (?, ?, ?, ?);");
            stmt.setString(1, name);
            stmt.setString(2, hash);
            stmt.setBoolean(3, is_locked);
            stmt.setBoolean(4, logged_in);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAccount(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM accounts WHERE username = ?;");
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public void changePassword(String name, String hash) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET password_hash = ? WHERE username = ?;");
            stmt.setString(1, hash);
            stmt.setString(2, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public void lockAccount(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET is_locked = true WHERE username = ?;");
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public void unlockAccount(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET is_locked = false WHERE username = ?;");
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Account> getAccount(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM accounts WHERE username = ?;");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new AccountClass(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getBoolean("is_locked"),
                        rs.getBoolean("logged_in"),
                        new Role(rs.getString("role_id"))
                ));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void login(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET logged_in = true WHERE username = ?;");
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public void logout(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET logged_in = false WHERE username = ?;");
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }
}
