package api.access_control;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class Capability {

    private final Set<String> permissions_new;

    public Capability() {
        permissions_new = new HashSet<>();
    }

    public boolean hasPermission(Resource resource, Operation operation) {
        String hash = getHash(String.valueOf(resource.getResourceId()), operation);

        return permissions_new.contains(hash);
    }

    public void addPermission(Resource resource, Operation operation) {
        String hash = getHash(String.valueOf(resource.getResourceId()), operation);

        permissions_new.add(hash);
    }

    private String getHash(String resourceId, Operation operation) {
        return Hashing.sha256().hashString(resourceId + " - " + operation, StandardCharsets.UTF_8).toString();
    }
}
