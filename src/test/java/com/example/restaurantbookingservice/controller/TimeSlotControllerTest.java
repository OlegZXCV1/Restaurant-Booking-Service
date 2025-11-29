package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.config.SecurityConfig;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.security.JwtAuthenticationEntryPoint;
import com.example.restaurantbookingservice.security.JwtRequestFilter;
import com.example.restaurantbookingservice.security.JwtTokenProvider;
import com.example.restaurantbookingservice.service.TimeSlotService;
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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeSlotController.class)
@Import(SecurityConfig.class)
public class TimeSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeSlotService timeSlotService;

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
    public void testGetAllTimeSlots() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4), table);
        List<TimeSlot> timeSlots = Arrays.asList(timeSlot1, timeSlot2);

        when(timeSlotService.getAllTimeSlots()).thenReturn(timeSlots);

        mockMvc.perform(get("/timeslots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    public void testGetTimeSlotById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlot.setId(1L);

        when(timeSlotService.getTimeSlotById(1L)).thenReturn(timeSlot);

        mockMvc.perform(get("/timeslots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    public void testGetTimeSlotsByRestaurantTableId() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        table.setId(1L);
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4), table);
        List<TimeSlot> timeSlots = Arrays.asList(timeSlot1, timeSlot2);

        when(timeSlotService.getTimeSlotsByRestaurantTableId(1L)).thenReturn(timeSlots);

        mockMvc.perform(get("/timeslots/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddTimeSlot() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlot.setId(1L);

        when(timeSlotService.addTimeSlot(any(TimeSlot.class))).thenReturn(timeSlot);

        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\": \"2025-11-15T10:00:00\", \"endTime\": \"2025-11-15T12:00:00\", \"restaurantTable\": {\"id\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteTimeSlot() throws Exception {
        mockMvc.perform(delete("/timeslots/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddTimeSlot_asUser_isForbidden() throws Exception {
        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\": \"2025-11-15T10:00:00\", \"endTime\": \"2025-11-15T12:00:00\", \"restaurantTable\": {\"id\": 1}}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteTimeSlot_asUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/timeslots/1"))
                .andExpect(status().isForbidden());
    }
}
