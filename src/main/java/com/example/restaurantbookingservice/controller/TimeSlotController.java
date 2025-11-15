package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timeslots")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotService.getAllTimeSlots();
    }

    @GetMapping("/{id}")
    public TimeSlot getTimeSlotById(@PathVariable Long id) {
        return timeSlotService.getTimeSlotById(id);
    }

    @GetMapping("/table/{restaurantTableId}")
    public List<TimeSlot> getTimeSlotsByRestaurantTableId(@PathVariable Long restaurantTableId) {
        return timeSlotService.getTimeSlotsByRestaurantTableId(restaurantTableId);
    }

    @PostMapping
    public TimeSlot addTimeSlot(@RequestBody TimeSlot timeSlot) {
        return timeSlotService.addTimeSlot(timeSlot);
    }

    @DeleteMapping("/{id}")
    public void deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
    }
}
