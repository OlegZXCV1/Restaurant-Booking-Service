package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.config.SecurityConfig;
import com.example.restaurantbookingservice.dto.RestaurantDto;
import com.example.restaurantbookingservice.security.JwtAuthenticationEntryPoint;
import com.example.restaurantbookingservice.security.JwtRequestFilter;
import com.example.restaurantbookingservice.security.JwtTokenProvider;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test-security")
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllRestaurants() throws Exception {
        RestaurantDto restaurant1 = new RestaurantDto();
        restaurant1.setName("Restaurant 1");
        RestaurantDto restaurant2 = new RestaurantDto();
        restaurant2.setName("Restaurant 2");
        List<RestaurantDto> restaurants = Arrays.asList(restaurant1, restaurant2);

        when(restaurantService.getAllRestaurants()).thenReturn(restaurants);

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Restaurant 1"))
                .andExpect(jsonPath("$[1].name").value("Restaurant 2"));
    }

    @Test
    @WithMockUser
    void getRestaurantById() throws Exception {
        RestaurantDto restaurant = new RestaurantDto();
        restaurant.setName("Restaurant 1");
        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurant);

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Restaurant 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addRestaurant() throws Exception {
        RestaurantDto restaurant = new RestaurantDto();
        restaurant.setName("Restaurant 1");
        when(restaurantService.addRestaurant(any(RestaurantDto.class))).thenReturn(restaurant);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRestaurant() throws Exception {
        mockMvc.perform(delete("/restaurants/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void addRestaurant_asUser_isForbidden() throws Exception {
        RestaurantDto restaurant = new RestaurantDto();
        restaurant.setName("Test Restaurant");

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteRestaurant_asUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/restaurants/1"))
                .andExpect(status().isForbidden());
    }
}
