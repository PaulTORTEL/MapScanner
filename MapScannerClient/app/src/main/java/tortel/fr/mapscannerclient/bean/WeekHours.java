package tortel.fr.mapscannerclient.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class WeekHours implements Serializable {

    Map<Integer, String> regularHours;
    Map<Integer, String> popularHours;

    public WeekHours() {
        regularHours = new TreeMap<>();
        popularHours = new TreeMap<>();
    }

    public Map<Integer, String> getRegularHours() {
        return regularHours;
    }

    public void setRegularHours(Map<Integer, String> regularHours) {
        this.regularHours = regularHours;
    }

    public Map<Integer, String> getPopularHours() {
        return popularHours;
    }

    public void setPopularHours(Map<Integer, String> popularHours) {
        this.popularHours = popularHours;
    }
}
