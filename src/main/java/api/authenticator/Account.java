package api.authenticator;

import api.access_control.Role;

public interface Account {

    boolean isLocked();

    boolean isLoggedIn();

    String getName();

    String getHash();

    void setHash(String hash);

    void setLoggedIn(boolean logged_in);

    Role getRole();
}
