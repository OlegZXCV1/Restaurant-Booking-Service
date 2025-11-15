package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Booking booking1 = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(2L, LocalDateTime.now(), 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getBookingById() {
        Booking booking = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertEquals("Customer 1", result.getCustomerName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getBookingsByRestaurantId() {
        Booking booking1 = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        Booking booking2 = new Booking(1L, LocalDateTime.now(), 4, "Customer 2", "0987654321", "c2@email.com");
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findByRestaurantId(1L)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByRestaurantId(1L);

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findByRestaurantId(1L);
    }

    @Test
    void addBooking() {
        Booking booking = new Booking(1L, LocalDateTime.now(), 2, "Customer 1", "1234567890", "c1@email.com");
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.addBooking(booking);

        assertEquals("Customer 1", result.getCustomerName());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void deleteBooking() {
        bookingService.deleteBooking(1L);
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}
