package tortel.fr.mapscannerclient.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Place implements Serializable {

    private String id;
    private String name;
    private String address;
    private String city;
    private String country;

    private int distance;
    private transient Bitmap image;

    private double lat;
    private double lng;

    private String category;

    private WeekHours weekHours;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getFullAddress() {
        return address + ", " + city + ", " + country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public WeekHours getWeekHours() {
        return weekHours;
    }

    public void setWeekHours(WeekHours weekHours) {
        this.weekHours = weekHours;
    }
}
