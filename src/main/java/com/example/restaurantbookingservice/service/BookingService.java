package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> getBookingsByTimeSlotId(Long timeSlotId) {
        return bookingRepository.findByTimeSlotId(timeSlotId);
    }

    public Booking addBooking(Booking booking) {
        List<Booking> bookings = bookingRepository.findByTimeSlotId(booking.getTimeSlot().getId());
        if (bookings.isEmpty()) {
            return bookingRepository.save(booking);
        }
        return null;
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
