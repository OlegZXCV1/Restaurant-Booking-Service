package com.example.restaurantbookingservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "restaurant_table_id")
    private RestaurantTable restaurantTable;

    public TimeSlot() {
    }

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime, RestaurantTable restaurantTable) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.restaurantTable = restaurantTable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }
}
