package impl;

import api.Account;

public class AccountClass implements Account {

    private final String name;
    private String hash;
    private final boolean locked;

    public AccountClass(String name, String hash, boolean locked) {
        this.name = name;
        this.hash = hash;
        this.locked = locked;
    }

    @Override
    public boolean isLocked() {
        return locked;
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
}
