package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getAllRestaurants() throws Exception {
        Restaurant restaurant1 = new Restaurant("Restaurant 1", "Address 1", "1234567890", "r1@email.com");
        Restaurant restaurant2 = new Restaurant("Restaurant 2", "Address 2", "0987654321", "r2@email.com");
        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);

        when(restaurantService.getAllRestaurants()).thenReturn(restaurants);

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Restaurant 1"))
                .andExpect(jsonPath("$[1].name").value("Restaurant 2"));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getRestaurantById() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant 1", "Address 1", "1234567890", "r1@email.com");
        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurant);

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Restaurant 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant 1", "Address 1", "1234567890", "r1@email.com");
        when(restaurantService.addRestaurant(restaurant)).thenReturn(restaurant);

        mockMvc.perform(post("/restaurants").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRestaurant() throws Exception {
        mockMvc.perform(delete("/restaurants/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
