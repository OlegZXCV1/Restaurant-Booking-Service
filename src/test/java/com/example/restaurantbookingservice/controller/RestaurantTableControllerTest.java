package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.config.SecurityConfig;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.security.JwtAuthenticationEntryPoint;
import com.example.restaurantbookingservice.security.JwtRequestFilter;
import com.example.restaurantbookingservice.security.JwtTokenProvider;
import com.example.restaurantbookingservice.service.RestaurantTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantTableController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test-security")
public class RestaurantTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantTableService restaurantTableService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                    User.withUsername("user").password("password").roles("USER").build(),
                    User.withUsername("admin").password("password").roles("ADMIN").build()
            );
        }

        @Bean
        public JwtRequestFilter jwtRequestFilter() {
            return new JwtRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
                    chain.doFilter(request, response);
                }
            };
        }
    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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

        mockMvc.perform(post("/tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableNumber\": 1, \"capacity\": 4, \"restaurant\": {\"id\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteTable() throws Exception {
        mockMvc.perform(delete("/tables/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddTable_asUser_isForbidden() throws Exception {
        mockMvc.perform(post("/tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableNumber\": 1, \"capacity\": 4, \"restaurant\": {\"id\": 1}}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteTable_asUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/tables/1"))
                .andExpect(status().isForbidden());
    }
}
