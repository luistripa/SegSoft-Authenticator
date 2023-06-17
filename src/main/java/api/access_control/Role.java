package api.access_control;

public record Role(String roleId) {

    @Override
    public String toString() {
        return "Role [roleId=" + roleId + "]";
    }
}
