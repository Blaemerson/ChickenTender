package com.chickentender.apk;

// This class holds all data and methods related to a room.
// A room is created through the CreateRoomFragment.
public class Room {
    private String name;

    // To connect to the host who set up the room.
    private String hostIP;

    private String[] IPs;
    // Radius (in meters or miles) from which to pull restaurants.
    private double radius;

    // Location broken up into longitude and latitude.
    private double latitude;
    private double longtitude;

    // To store restaurants that were collected during room creation.
    private Restaurant[] restaurants;

    // Constructor
    public Room(String name, String hostIP, double radius, double latitude, double longtitude) {
        this.name = name;
        this.hostIP = hostIP;
        this.radius = radius;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    // Alternate constructor with array of restaurants parameter
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

    // Method to add a single restaurant to the list.
    // May or may not be necessary.
    public void addRestaurant(Restaurant restaurant) {
        Restaurant[] updated = new Restaurant[restaurants.length + 1];
        for (int i = 0; i < restaurants.length; i++) {
            updated[i] = restaurants[i];
        }
        updated[updated.length] = restaurant;
        setRestaurants(updated);
    }
}
