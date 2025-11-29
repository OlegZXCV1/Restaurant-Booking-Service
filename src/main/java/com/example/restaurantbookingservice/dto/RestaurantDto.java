package com.example.restaurantbookingservice.dto;

import lombok.Data;

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
}
