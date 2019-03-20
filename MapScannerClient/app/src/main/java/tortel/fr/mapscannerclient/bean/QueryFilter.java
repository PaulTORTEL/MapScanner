package tortel.fr.mapscannerclient.bean;

import java.io.Serializable;
import java.util.Set;

public class QueryFilter implements Serializable {
    private int type;
    private Set<String> tags;
    private int radius;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
