package com.chickentender.apk;

public class Restaurant {
    private String name;
    private String vicinity;
    private double latitude;
    private double longitude;
    private String userRating;
    private String photo;

    public Restaurant(String name, String vicinity, double latitude, double longitude) {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Restaurant(String name, String vicinity, double latitude, double longitude, String userRating, String photo) {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRating = userRating;
        this.photo = photo;
    }

    @Override
    public String toString() {
        return name + " " + vicinity + " " + latitude + " "  + longitude + " "  + photo;
    }

    public String getUserRating() { return userRating; }
    public String getName() {
        return name;
    }
    public String getVicinity() {
        return vicinity;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double[] getLocation() {
        return new double[]{getLatitude(), getLongitude()};
    }

    public String getPhoto() {
        return photo;
    }
}
