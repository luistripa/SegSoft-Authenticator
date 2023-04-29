package impl;

import api.Account;

public class AccountClass implements Account {

    private final String name;
    private String hash;
    private final boolean locked;
    private boolean logged_in;

    public AccountClass(String name, String hash, boolean locked) {
        this.name = name;
        this.hash = hash;
        this.locked = locked;
        this.logged_in = false;
    }

    public AccountClass(String name, String hash, boolean locked, boolean logged_in) {
        this.name = name;
        this.hash = hash;
        this.locked = locked;
        this.logged_in = logged_in;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public boolean isLoggedIn() {
        return logged_in;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public void setLoggedIn(boolean logged_in) {
        this.logged_in = logged_in;
    }
}
