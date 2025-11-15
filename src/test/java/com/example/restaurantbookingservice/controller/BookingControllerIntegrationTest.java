package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.service.BookingService;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.example.restaurantbookingservice.service.RestaurantTableService;
import com.example.restaurantbookingservice.service.TimeSlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Test
    public void testGetAllBookings() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking booking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        bookingService.addBooking(booking);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"));
    }

    @Test
    public void testGetBookingById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking booking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking savedBooking = bookingService.addBooking(booking);

        mockMvc.perform(get("/bookings/" + savedBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    public void testAddBooking() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking booking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    public void testDeleteBooking() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking booking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking savedBooking = bookingService.addBooking(booking);

        mockMvc.perform(delete("/bookings/" + savedBooking.getId()))
                .andExpect(status().isOk());
    }
}
