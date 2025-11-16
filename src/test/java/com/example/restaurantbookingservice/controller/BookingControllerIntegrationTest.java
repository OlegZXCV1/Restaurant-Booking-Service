package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.model.ERole;
import com.example.restaurantbookingservice.model.Restaurant;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.Role;
import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.model.User;
import com.example.restaurantbookingservice.repository.RoleRepository;
import com.example.restaurantbookingservice.repository.UserRepository;
import com.example.restaurantbookingservice.security.services.UserDetailsImpl;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.example.restaurantbookingservice.service.RestaurantTableService;
import com.example.restaurantbookingservice.service.TimeSlotService;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private TimeSlotService timeSlotService;

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

    private Booking createBooking(TimeSlot timeSlot, User user, int numberOfPeople, String customerName, String customerPhone, String customerEmail) throws Exception {
        Booking booking = new Booking(timeSlot, user, numberOfPeople, customerName, customerPhone, customerEmail);
        String responseString = mockMvc.perform(post("/bookings")
                        .with(user(UserDetailsImpl.build(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseString, Booking.class);
    }

    @Test
    public void testGetAllBookings() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        createBooking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");

        mockMvc.perform(get("/bookings").with(user(UserDetailsImpl.build(adminUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"));
    }

    @Test
    public void testGetBookingById() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking savedBooking = createBooking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");

        mockMvc.perform(get("/bookings/" + savedBooking.getId()).with(user(UserDetailsImpl.build(testUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    public void testAddBooking() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking booking = new Booking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");

        mockMvc.perform(post("/bookings")
                        .with(user(UserDetailsImpl.build(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    public void testDeleteBooking() throws Exception {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", "1234567890", "test@test.com");
        restaurantService.addRestaurant(restaurant);
        RestaurantTable table = new RestaurantTable(1, 4, restaurant);
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        Booking savedBooking = createBooking(timeSlot, testUser, 2, "Customer 1", "1234567890", "c1@email.com");

        mockMvc.perform(delete("/bookings/" + savedBooking.getId()).with(user(UserDetailsImpl.build(testUser))))
                .andExpect(status().isOk());
    }
}