package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/timeslot/{timeSlotId}")
    public List<Booking> getBookingsByTimeSlotId(@PathVariable Long timeSlotId) {
        return bookingService.getBookingsByTimeSlotId(timeSlotId);
    }

    @PostMapping
    public ResponseEntity<Booking> addBooking(@RequestBody Booking booking) {
        Booking newBooking = bookingService.addBooking(booking);
        if (newBooking != null) {
            return ResponseEntity.ok(newBooking);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
