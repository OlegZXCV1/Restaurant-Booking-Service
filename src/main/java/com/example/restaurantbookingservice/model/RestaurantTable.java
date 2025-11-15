package com.example.restaurantbookingservice.model;

import jakarta.persistence.*;

@Entity
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int tableNumber;
    private int capacity;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public RestaurantTable() {
    }

    public RestaurantTable(int tableNumber, int capacity, Restaurant restaurant) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.restaurant = restaurant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
