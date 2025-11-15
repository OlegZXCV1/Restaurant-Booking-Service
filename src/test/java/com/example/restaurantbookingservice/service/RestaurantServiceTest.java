package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {

    @InjectMocks
    RestaurantService restaurantService;

    @Mock
    RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRestaurants() {
        Restaurant restaurant1 = new Restaurant("Restaurant 1", "Address 1", "1234567890", "r1@email.com");
        Restaurant restaurant2 = new Restaurant("Restaurant 2", "Address 2", "0987654321", "r2@email.com");
        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);

        when(restaurantRepository.findAll()).thenReturn(restaurants);

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertEquals(2, result.size());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void getRestaurantById() {
        Restaurant restaurant = new Restaurant("Restaurant 1", "Address 1", "1234567890", "r1@email.com");
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurantById(1L);

        assertEquals("Restaurant 1", result.getName());
        verify(restaurantRepository, times(1)).findById(1L);
    }

    @Test
    void addRestaurant() {
        Restaurant restaurant = new Restaurant("Restaurant 1", "Address 1", "1234567890", "r1@email.com");
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        Restaurant result = restaurantService.addRestaurant(restaurant);

        assertEquals("Restaurant 1", result.getName());
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    void deleteRestaurant() {
        restaurantService.deleteRestaurant(1L);
        verify(restaurantRepository, times(1)).deleteById(1L);
    }
}
