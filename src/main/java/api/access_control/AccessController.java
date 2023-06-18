package api.access_control;

import api.access_control.exceptions.AccessControlException;
import api.access_control.exceptions.OperationNotFoundException;
import api.access_control.exceptions.RoleAlreadyExistsException;
import api.access_control.exceptions.RoleNotFoundException;
import api.authenticator.Account;
import api.authenticator.exceptions.UndefinedAccountException;

public interface AccessController {

    int currentCode();

    void generateNewCode();

    Role newRole(String roleId) throws RoleAlreadyExistsException;

    void setRole(Account account, Role role) throws RoleNotFoundException, UndefinedAccountException;

    void grantPermission(Role role, Resource resource, Operation operation) throws RoleNotFoundException, OperationNotFoundException;

    void grantPermission(Account account, Resource resource, Operation operation) throws UndefinedAccountException, OperationNotFoundException;

    void revokePermission(Role role, Resource resource, Operation operation) throws RoleNotFoundException, OperationNotFoundException;

    void revokePermission(Account account, Resource resource, Operation operation) throws OperationNotFoundException;

    Capability getCapability(Account account);

    Capability checkPermission(Capability capability, Resource resource, Operation operation) throws AccessControlException;
}
