package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.service.RestaurantTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantTableController.class)
public class RestaurantTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantTableService restaurantTableService;

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void testGetAllTables() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table1 = new RestaurantTable(1, 4, restaurant);
        RestaurantTable table2 = new RestaurantTable(2, 2, restaurant);
        List<RestaurantTable> tables = Arrays.asList(table1, table2);

        when(restaurantTableService.getAllTables()).thenReturn(tables);

        mockMvc.perform(get("/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tableNumber").value(1))
                .andExpect(jsonPath("$[1].tableNumber").value(2));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void testGetTableById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        table.setId(1L);

        when(restaurantTableService.getTableById(1L)).thenReturn(table);

        mockMvc.perform(get("/tables/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void testGetTablesByRestaurantId() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurant.setId(1L);
        RestaurantTable table1 = new RestaurantTable(1, 4, restaurant);
        RestaurantTable table2 = new RestaurantTable(2, 2, restaurant);
        List<RestaurantTable> tables = Arrays.asList(table1, table2);

        when(restaurantTableService.getTablesByRestaurantId(1L)).thenReturn(tables);

        mockMvc.perform(get("/tables/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tableNumber").value(1))
                .andExpect(jsonPath("$[1].tableNumber").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddTable() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        table.setId(1L);

        when(restaurantTableService.addTable(any(RestaurantTable.class))).thenReturn(table);

        mockMvc.perform(post("/tables").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableNumber\": 1, \"capacity\": 4, \"restaurant\": {\"id\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteTable() throws Exception {
        mockMvc.perform(delete("/tables/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
