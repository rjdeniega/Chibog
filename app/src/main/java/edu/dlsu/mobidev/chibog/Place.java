package edu.dlsu.mobidev.chibog;

/**
 * Created by Ira on 10/21/2017.
 */

public class Place {
    private String name, location, imageUrl;
    private Double lat, lng;

    public Place(){

    }

    public Place(String name, String location, String imageUrl, Double lat, Double lng) {
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
