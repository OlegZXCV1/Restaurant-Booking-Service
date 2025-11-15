package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void getAllBookings() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(2L, LocalDateTime.now(), 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"))
                .andExpect(jsonPath("$[1].customerName").value("Customer 2"));
    }

    @Test
    void getBookingById() throws Exception {
        Booking booking = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    void getBookingsByRestaurantId() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(1L, LocalDateTime.now(), 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getBookingsByRestaurantId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void addBooking() throws Exception {
        Booking booking = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingService.addBooking(booking)).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBooking() throws Exception {
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk());
    }
}
