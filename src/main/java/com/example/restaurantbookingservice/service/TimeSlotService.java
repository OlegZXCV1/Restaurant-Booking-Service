package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id).orElse(null);
    }

    public List<TimeSlot> getTimeSlotsByRestaurantTableId(Long restaurantTableId) {
        return timeSlotRepository.findByRestaurantTableId(restaurantTableId);
    }

    public TimeSlot addTimeSlot(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    public void deleteTimeSlot(Long id) {
        timeSlotRepository.deleteById(id);
    }
}
