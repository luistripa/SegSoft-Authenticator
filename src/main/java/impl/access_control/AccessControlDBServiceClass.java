package impl.access_control;

import api.AccessControlDBService;
import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.Resource;
import api.access_control.Role;
import api.authenticator.Account;

import java.sql.*;

public class AccessControlDBServiceClass implements AccessControlDBService {

    public static final String DB_NAME = "database.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    private static AccessControlDBService instance;

    public static AccessControlDBService getInstance() {
        if (instance == null) {
            instance = new AccessControlDBServiceClass();
        }
        return instance;
    }

    private AccessControlDBServiceClass() {
        try {
            Class.forName("org.sqlite.JDBC");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Role newRole(String roleId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO roles (role_id) VALUES (?);");
            stmt.setString(1, roleId);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("SELECT * FROM roles WHERE role_id = ?;");
            stmt.setString(1, roleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Role(rs.getString("role_id"));
            }
            return null;
        }
    }

    @Override
    public Role getRole(String roleId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM roles WHERE role_id = ?;");
            stmt.setString(1, roleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Role(rs.getString("role_id"));
            }
            return null;
        }
    }

    @Override
    public Operation getOperation(String operationId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM operations WHERE operation_id = ?;");
            stmt.setString(1, operationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Operation.valueOf(rs.getString("operation_id"));
            }
            return null;
        }
    }

    @Override
    public void setRole(Account account, Role role) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET role_id = ? WHERE username = ?;");
            stmt.setString(1, role.roleId());
            stmt.setString(2, account.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void grantPermission(Role role, Resource resource, Operation operation) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES (?, ?, ?);");
            stmt.setString(1, role.roleId());
            stmt.setInt(2, resource.getResourceId());
            stmt.setString(3, operation.toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public void grantPermission(Account account, Resource resource, Operation operation) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO account_permissions (username, resource_id, operation_id) VALUES (?, ?, ?);");
            stmt.setString(1, account.getName());
            stmt.setInt(2, resource.getResourceId());
            stmt.setString(3, operation.toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public void revokePermission(Role role, Resource resource, Operation operation) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM role_permissions WHERE role_id = ? AND resource_id = ? AND operation_id = ?;");
            stmt.setString(1, role.roleId());
            stmt.setInt(2, resource.getResourceId());
            stmt.setString(3, operation.toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public void revokePermission(Account account, Resource resource, Operation operation) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM account_permissions WHERE username = ? AND resource_id = ? AND operation_id = ?;");
            stmt.setString(1, account.getName());
            stmt.setInt(2, resource.getResourceId());
            stmt.setString(3, operation.toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public Capability getCapability(Account account) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Role role = account.getRole();

            Capability capability = new Capability();

            // Get role-specific permissions
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM role_permissions WHERE role_id = ?;");
            stmt.setString(1, role.roleId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Resource resource = new PageResource(rs.getInt("resource_id"));
                Operation operation = Operation.valueOf(rs.getString("operation_id"));
                capability.addPermission(resource, operation);
            }

            // Get account-specific permissions
            stmt = conn.prepareStatement("SELECT * FROM account_permissions WHERE username = ?;");
            stmt.setString(1, account.getName());
            rs = stmt.executeQuery();

            while (rs.next()) {
                Resource resource = new PageResource(rs.getInt("resource_id"));
                Operation operation = Operation.valueOf(rs.getString("operation_id"));
                capability.addPermission(resource, operation);
            }

            return capability;
        }
    }
}
