package com.example.restaurantbookingservice.repository;

import com.example.restaurantbookingservice.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByRestaurantTableId(Long restaurantTableId);
}
