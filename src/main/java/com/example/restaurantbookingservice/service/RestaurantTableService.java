package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantTableService {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    public List<RestaurantTable> getAllTables() {
        return restaurantTableRepository.findAll();
    }

    public RestaurantTable getTableById(Long id) {
        return restaurantTableRepository.findById(id).orElse(null);
    }

    public List<RestaurantTable> getTablesByRestaurantId(Long restaurantId) {
        return restaurantTableRepository.findByRestaurantId(restaurantId);
    }

    public RestaurantTable addTable(RestaurantTable table) {
        return restaurantTableRepository.save(table);
    }

    public void deleteTable(Long id) {
        restaurantTableRepository.deleteById(id);
    }
}
