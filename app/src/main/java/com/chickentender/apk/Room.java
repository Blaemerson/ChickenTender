package com.chickentender.apk;

import java.util.List;

// This class holds all data and methods related to a room.
// A room is created through the CreateRoomFragment.
public class Room
{
    private String roomID;
    // To connect to the host who set up the room.
    private String hostID;
    private List<String> members;
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
        this.roomID = name;
        this.hostID = hostID;
        this.members = members;
        this.restaurants= restaurants;
    }

    public String getRoomID()
    {
        return roomID;
    }

    public void setRestaurants(List<Restaurant> restaurants)
    {
        this.restaurants = restaurants;
    }
    public List<Restaurant> getRestaurants()
    {
        return restaurants;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
    public List<String> getMembers() {
        return this.members;
    }
    // Method to add a single restaurant to the list.
    public void addRestaurant(Restaurant restaurant)
    {
        restaurants.add(restaurant);
    }
}
