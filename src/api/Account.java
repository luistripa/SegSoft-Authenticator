package api;

public interface Account {

    boolean isLocked();

    String getName();

    String getHash();

    void setHash(String hash);
}
