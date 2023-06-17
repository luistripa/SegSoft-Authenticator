package impl.access_control;

import api.AccessControlDBService;
import api.access_control.*;
import api.access_control.exceptions.AccessControlException;
import api.access_control.exceptions.OperationNotFoundException;
import api.access_control.exceptions.RoleAlreadyExistsException;
import api.access_control.exceptions.RoleNotFoundException;
import api.authenticator.Account;

import java.sql.SQLException;

public class AccessControllerClass implements AccessController {

    private static final AccessController INSTANCE = new AccessControllerClass();
    public static AccessController getInstance() {
        return INSTANCE;
    }


    private final AccessControlDBService accessControlService;

    public AccessControllerClass() {
        accessControlService = AccessControlDBServiceClass.getInstance();
    }

    @Override
    public Role newRole(String roleId) throws RoleAlreadyExistsException {
        try {
            if(accessControlService.getRole(roleId) != null)
                throw new RoleAlreadyExistsException();

            return accessControlService.newRole(roleId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRole(Account account, Role role) throws RoleNotFoundException {
        try {
            if(accessControlService.getRole(role.roleId()) == null)
                throw new RoleNotFoundException();

            accessControlService.setRole(account, role);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void grantPermission(Role role, Resource resource, Operation operation) throws RoleNotFoundException, OperationNotFoundException {
        try {
            if(accessControlService.getRole(role.roleId()) == null)
                throw new RoleNotFoundException();

            if(accessControlService.getOperation(operation.name()) == null)
                throw new OperationNotFoundException();

            accessControlService.grantPermission(role, resource, operation);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void grantPermission(Account account, Resource resource, Operation operation) throws OperationNotFoundException {
        try {
            if(accessControlService.getOperation(operation.name()) == null)
                throw new OperationNotFoundException();

            accessControlService.grantPermission(account, resource, operation);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void revokePermission(Role role, Resource resource, Operation operation) throws RoleNotFoundException, OperationNotFoundException {
        try {
            if(accessControlService.getRole(role.roleId()) == null)
                throw new RoleNotFoundException();

            if(accessControlService.getOperation(operation.name()) == null)
                throw new OperationNotFoundException();

            accessControlService.revokePermission(role, resource, operation);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void revokePermission(Account account, Resource resource, Operation operation) throws OperationNotFoundException {
        try {
            if(accessControlService.getOperation(operation.name()) == null)
                throw new OperationNotFoundException();

            accessControlService.revokePermission(account, resource, operation);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Capability getCapability(Account account) {
        try {
            return accessControlService.getCapability(account);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkPermission(Capability capability, Resource resource, Operation operation) throws AccessControlException {
        if(!capability.hasPermission(resource, operation)) {
            throw new AccessControlException();
        }
    }
}
