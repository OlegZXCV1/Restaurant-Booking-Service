package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.ERole;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.Role;
import com.example.restaurantbookingservice.model.User;
import com.example.restaurantbookingservice.repository.RoleRepository;
import com.example.restaurantbookingservice.repository.UserRepository;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));

        testUser = new User("testuser", passwordEncoder.encode("password"));
        testUser.setRoles(Collections.singleton(userRole));
        userRepository.save(testUser);

        adminUser = new User("adminuser", passwordEncoder.encode("adminpassword"));
        adminUser.setRoles(Collections.singleton(adminRole));
        userRepository.save(adminUser);
    }

    @Test
    public void testGetAllRestaurants() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);

        mockMvc.perform(get("/restaurants").with(user(testUser.getUsername()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));
    }

    @Test
    public void testGetRestaurantById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        Restaurant savedRestaurant = restaurantService.addRestaurant(restaurant);

        mockMvc.perform(get("/restaurants/" + savedRestaurant.getId()).with(user(testUser.getUsername()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    public void testAddRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");

        mockMvc.perform(post("/restaurants")
                        .with(user(adminUser.getUsername()).roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    public void testDeleteRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        Restaurant savedRestaurant = restaurantService.addRestaurant(restaurant);

        mockMvc.perform(delete("/restaurants/" + savedRestaurant.getId()).with(user(adminUser.getUsername()).roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
