package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.dto.BookingDto;
import com.example.restaurantbookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/timeslot/{timeSlotId}")
    public List<BookingDto> getBookingsByTimeSlotId(@PathVariable Long timeSlotId) {
        return bookingService.getBookingsByTimeSlotId(timeSlotId);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDto> addBooking(@RequestBody BookingDto bookingDto) {
        BookingDto newBooking = bookingService.addBooking(bookingDto);
        if (newBooking != null) {
            return ResponseEntity.ok(newBooking);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
