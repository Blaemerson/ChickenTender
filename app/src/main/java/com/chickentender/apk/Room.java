package com.chickentender.apk;

import java.time.format.ResolverStyle;

public class Room {
    private String name;
    private String hostIP;

    private double radius;
    private double latitude;
    private double longtitude;

    private Restaurant[] restaurants;

    public Room(String name, String hostIP, double radius, double latitude, double longtitude) {
        this.name = name;
        this.hostIP = hostIP;
        this.radius = radius;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
    public Room(String name, Restaurant[] restaurants, String hostIP, double radius, double latitude, double longtitude) {
        this.name = name;
        this.hostIP = hostIP;
        this.radius = radius;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.restaurants= restaurants;
    }
    public String getName() {
        return name;
    }

    public void setRestaurants(Restaurant[] restaurants) {
        this.restaurants = restaurants;
    }
    public Restaurant[] getRestaurants() {
        return restaurants;
    }

    public void addRestaurant(Restaurant restaurant) {
        Restaurant[] updated = new Restaurant[restaurants.length + 1];
        for (int i = 0; i < restaurants.length; i++) {
            updated[i] = restaurants[i];
        }
        updated[updated.length] = restaurant;
        setRestaurants(updated);
    }
}
