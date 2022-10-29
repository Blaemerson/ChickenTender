package com.chickentender.apk;

public class Restaurant {
    private String name;
    private String rawJsonString;
    private String vicinity;
    private double latitude;
    private double longitude;
    private String photo;

    public Restaurant(String name, String vicinity, double latitude, double longitude, String photo) {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    public Restaurant(String name, double latitude, double longitude, String photo) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    @Override
    public String toString() {
        return name + " " + vicinity + " " + latitude + " "  + longitude + " "  + photo;
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
