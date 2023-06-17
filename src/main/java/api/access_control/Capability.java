package api.access_control;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Capability {

    private final Map<Operation, Set<Resource>> permissions;

    public Capability() {
        permissions = new HashMap<>();
    }

    public boolean hasPermission(Resource resource, Operation operation) {
        return permissions.containsKey(operation) && permissions.get(operation).contains(resource);
    }

    public void addPermission(Resource resource, Operation operation) {
        permissions.putIfAbsent(operation, new HashSet<>());
        permissions.get(operation).add(resource);
    }

    @Override
    public String toString() {
        return "Capability [permissions=" + permissions + "]";
    }
}
