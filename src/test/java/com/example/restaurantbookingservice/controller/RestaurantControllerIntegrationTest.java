package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.dto.RestaurantDto;
import com.example.restaurantbookingservice.service.RestaurantService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    @WithMockUser
    public void testGetAllRestaurants() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        restaurantService.addRestaurant(restaurantDto);

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));
    }

    @Test
    @WithMockUser
    public void testGetRestaurantById() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);

        mockMvc.perform(get("/restaurants/" + savedRestaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddRestaurant() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddRestaurant_asUser_isForbidden() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteRestaurant() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);

        mockMvc.perform(delete("/restaurants/" + savedRestaurant.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteRestaurant_asUser_isForbidden() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);

        mockMvc.perform(delete("/restaurants/" + savedRestaurant.getId()))
                .andExpect(status().isForbidden());
    }
}
