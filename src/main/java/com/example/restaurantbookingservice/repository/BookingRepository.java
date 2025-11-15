package com.example.restaurantbookingservice.repository;

import com.example.restaurantbookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTimeSlotId(Long timeSlotId);
    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByUserIdAndId(Long userId, Long bookingId);
}
