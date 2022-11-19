package com.chickentender.apk;

import java.util.List;

// This class holds all data and methods related to a room.
// A room is created through the CreateRoomFragment.
public class Room
{
    private String name;
    // To connect to the host who set up the room.
    private String hostID;
    private String[] IPs;
    // Radius (in meters or miles) from which to pull restaurants.
    private double radius;
    // Location broken up into longitude and latitude.
    private double latitude;
    private double longtitude;
    // To store restaurants that were collected during room creation.
    private List<Restaurant> restaurants;

    // Constructor
    public Room(String name, List<Restaurant> restaurants, List<String> members, String hostID)
    {
        this.name = name;
        this.hostID = hostID;
        this.restaurants= restaurants;
    }

    public String getName()
    {
        return name;
    }

    public void setRestaurants(List<Restaurant> restaurants)
    {
        this.restaurants = restaurants;
    }
    public List<Restaurant> getRestaurants()
    {
        return restaurants;
    }

    // Method to add a single restaurant to the list.
    public void addRestaurant(Restaurant restaurant)
    {
        restaurants.add(restaurant);
    }
}
