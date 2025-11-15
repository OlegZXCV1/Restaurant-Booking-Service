package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.model.User;
import com.example.restaurantbookingservice.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password"); // Password will be encoded by service
        testUser.setId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBookings() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        Booking booking1 = new Booking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(timeSlot, testUser, 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getBookingById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        Booking booking = new Booking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");
        booking.setId(1L);
        when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBookingsByTimeSlotId() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlot.setId(1L);
        Booking booking1 = new Booking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(timeSlot, testUser, 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getBookingsByTimeSlotId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/timeslot/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void addBooking() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        Booking booking = new Booking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingService.addBooking(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void addBooking_whenTimeSlotIsTaken() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        Booking booking = new Booking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingService.addBooking(any(Booking.class))).thenReturn(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void deleteBooking() throws Exception {
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk());
    }
}