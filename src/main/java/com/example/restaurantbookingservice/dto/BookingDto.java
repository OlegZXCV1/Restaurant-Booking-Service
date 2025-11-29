package com.example.restaurantbookingservice.dto;

import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private Long timeSlotId;
    private int numberOfPeople;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
}
