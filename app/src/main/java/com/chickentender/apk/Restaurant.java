package com.chickentender.apk;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Restaurant implements Serializable
{
    private String name;
    private String restaurantID;
    private String vicinity;
    private double latitude;
    private double longitude;
    private String userRating;
    private String photo;

    public Restaurant(String name, String vicinity, double latitude, double longitude)
    {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Restaurant(String name, String vicinity, double latitude, double longitude, String userRating, String photo)
    {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRating = userRating;
        this.photo = photo;
    }
    public Restaurant(String name, String restaurantID, String vicinity, double latitude, double longitude, String userRating, String photo)
    {
        this.name = name;
        this.restaurantID = restaurantID;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRating = userRating;
        this.photo = photo;
    }

    @NonNull
    @Override
    public String toString()
    {
        return name + " " + vicinity + " " + latitude + " "  + longitude + " "  + photo;
    }

    public String getUserRating()
    {
        return userRating;
    }
    public String getName()
    {
        return name;
    }
    public String getVicinity()
    {
        return vicinity;
    }
    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public String getPhoto()
    {
        return photo;
    }
    public String getRestaurantID()
    {
        return restaurantID;
    }
}
