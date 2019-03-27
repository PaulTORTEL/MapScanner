package tortel.fr.mapscanner.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class HoursVenue {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "venue_id")
    private String venueId;

    private String hours;
    @ColumnInfo(name = "popular_hours")
    private String popularHours;
    private long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getPopularHours() {
        return popularHours;
    }

    public void setPopularHours(String popularHours) {
        this.popularHours = popularHours;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
