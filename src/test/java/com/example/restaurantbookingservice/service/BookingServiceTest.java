package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @InjectMocks
    BookingService bookingService;

    @Mock
    BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBookings() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        Booking booking1 = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(timeSlot, 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getBookingById() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        Booking booking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertEquals("Customer 1", result.getCustomerName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getBookingsByTimeSlotId() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlot.setId(1L);
        Booking booking1 = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(timeSlot, 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findByTimeSlotId(1L)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByTimeSlotId(1L);

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findByTimeSlotId(1L);
    }

    @Test
    void addBooking() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlot.setId(1L);
        Booking booking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");

        when(bookingRepository.findByTimeSlotId(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.addBooking(booking);

        assertEquals("Customer 1", result.getCustomerName());
        verify(bookingRepository, times(1)).findByTimeSlotId(1L);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void addBooking_whenTimeSlotIsTaken() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlot.setId(1L);
        Booking existingBooking = new Booking(timeSlot, 2, "Customer 1", "1234567890", "c1@email.com");
        Booking newBooking = new Booking(timeSlot, 4, "Customer 2", "0987654321", "c2@email.com");

        when(bookingRepository.findByTimeSlotId(1L)).thenReturn(Arrays.asList(existingBooking));

        Booking result = bookingService.addBooking(newBooking);

        assertNull(result);
        verify(bookingRepository, times(1)).findByTimeSlotId(1L);
        verify(bookingRepository, never()).save(newBooking);
    }

    @Test
    void deleteBooking() {
        bookingService.deleteBooking(1L);
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}
