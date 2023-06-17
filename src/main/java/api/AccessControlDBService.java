package api;

import api.access_control.Capability;
import api.access_control.Operation;
import api.access_control.Resource;
import api.access_control.Role;
import api.authenticator.Account;

import java.sql.SQLException;

public interface AccessControlDBService {

    Role newRole(String roleId) throws SQLException;

    Role getRole(String roleId) throws SQLException;

    Operation getOperation(String operationId) throws SQLException;

    void setRole(Account account, Role role) throws SQLException;

    void grantPermission(Role role, Resource resource, Operation operation) throws SQLException;

    void grantPermission(Account account, Resource resource, Operation operation) throws SQLException;

    void revokePermission(Role role, Resource resource, Operation operation) throws SQLException;

    void revokePermission(Account account, Resource resource, Operation operation) throws SQLException;

    Capability getCapability(Account account) throws SQLException;
}
