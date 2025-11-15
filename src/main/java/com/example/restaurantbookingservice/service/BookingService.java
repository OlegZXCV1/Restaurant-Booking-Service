package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.model.User;
import com.example.restaurantbookingservice.repository.BookingRepository;
import com.example.restaurantbookingservice.repository.UserRepository;
import com.example.restaurantbookingservice.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return bookingRepository.findById(id).orElse(null);
        } else {
            return bookingRepository.findByUserIdAndId(userDetails.getId(), id).orElse(null);
        }
    }

    public List<Booking> getBookingsByTimeSlotId(Long timeSlotId) {
        return bookingRepository.findByTimeSlotId(timeSlotId);
    }

    public Booking addBooking(Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByTimeSlotId(booking.getTimeSlot().getId());
        if (bookings.isEmpty()) {
            booking.setUser(user);
            return bookingRepository.save(booking);
        }
        return null;
    }

    public void deleteBooking(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            bookingRepository.deleteById(id);
        } else {
            bookingRepository.findByUserIdAndId(userDetails.getId(), id).ifPresent(bookingRepository::delete);
        }
    }
}
