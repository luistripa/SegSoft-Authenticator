package api.access_control;

import api.authenticator.Account;
import api.authenticator.exceptions.AuthenticatorException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Capability {

    private final Set<String> permissions;
    private final Account account;
    private int code;

    public Capability(Account account) {
        permissions = new HashSet<>();
        this.account = account;
        code = 0;
    }

    public Capability(Account account, int code) {
        permissions = new HashSet<>();
        this.account = account;
        this.code = code;
    }

    public Account getAccount() {
        return account;
    }

    public boolean validateCode(int code) {
        return this.code == code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean hasPermission(Resource resource, Operation operation) {
        String hash = getHash(String.valueOf(resource.getResourceId()), operation);

        return permissions.contains(hash);
    }

    public void addPermission(Resource resource, Operation operation) {
        String hash = getHash(String.valueOf(resource.getResourceId()), operation);

        permissions.add(hash);
    }

    private String getHash(String resourceId, Operation operation) {
        return Hashing.sha256().hashString(resourceId + " - " + operation, StandardCharsets.UTF_8).toString();
    }
}
