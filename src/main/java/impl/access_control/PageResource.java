package impl.access_control;

import api.access_control.Resource;

public class PageResource implements Resource {

    private final int id;

    public PageResource(int id) {
        this.id = id;
    }

    @Override
    public int getResourceId() {
        return id;
    }

    @Override
    public String toString() {
        return "PageResource{" +
                "id=" + id +
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PageResource other) {
            return id == other.id;
        }

        return false;
    }
}
