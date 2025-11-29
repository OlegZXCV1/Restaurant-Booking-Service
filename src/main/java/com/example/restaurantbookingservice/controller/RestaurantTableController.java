package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class RestaurantTableController {

    @Autowired
    private RestaurantTableService restaurantTableService;

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return restaurantTableService.getAllTables();
    }

    @GetMapping("/{id}")
    public RestaurantTable getTableById(@PathVariable Long id) {
        return restaurantTableService.getTableById(id);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<RestaurantTable> getTablesByRestaurantId(@PathVariable Long restaurantId) {
        return restaurantTableService.getTablesByRestaurantId(restaurantId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantTable addTable(@RequestBody RestaurantTable table) {
        return restaurantTableService.addTable(table);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTable(@PathVariable Long id) {
        restaurantTableService.deleteTable(id);
    }
}
