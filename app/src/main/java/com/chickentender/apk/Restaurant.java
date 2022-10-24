package com.chickentender.apk;

public class Restaurant {
    private String name;
    private double latitude;
    private double longitude;
    private String photo;

    public Restaurant(String name, double latitude, double longitude, String photo) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    public String getName() {
        return name;
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
