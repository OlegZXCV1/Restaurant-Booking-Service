package com.example.restaurantbookingservice.repository;

import com.example.restaurantbookingservice.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByRestaurantId(Long restaurantId);
}
