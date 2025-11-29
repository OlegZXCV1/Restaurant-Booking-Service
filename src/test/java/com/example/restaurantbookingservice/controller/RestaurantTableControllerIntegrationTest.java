package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.ERole;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.Role;
import com.example.restaurantbookingservice.model.User;
import com.example.restaurantbookingservice.repository.RoleRepository;
import com.example.restaurantbookingservice.repository.UserRepository;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.example.restaurantbookingservice.service.RestaurantTableService;
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
public class RestaurantTableControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantTableService restaurantTableService;

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
    public void testGetAllTables() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);

        mockMvc.perform(get("/tables").with(user(testUser.getUsername()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tableNumber").value(1));
    }

    @Test
    public void testGetTableById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        RestaurantTable savedTable = restaurantTableService.addTable(table);

        mockMvc.perform(get("/tables/" + savedTable.getId()).with(user(testUser.getUsername()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value(1));
    }

    @Test
    public void testGetTablesByRestaurantId() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);

        mockMvc.perform(get("/tables/restaurant/" + restaurant.getId()).with(user(testUser.getUsername()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tableNumber").value(1));
    }

    @Test
    public void testAddTable() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);

        mockMvc.perform(post("/tables")
                        .with(user(adminUser.getUsername()).roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(table)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value(1));
    }

    @Test
    public void testDeleteTable() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        RestaurantTable savedTable = restaurantTableService.addTable(table);

        mockMvc.perform(delete("/tables/" + savedTable.getId()).with(user(adminUser.getUsername()).roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
