package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.dto.BookingDto;
import com.example.restaurantbookingservice.dto.RestaurantDto;
import com.example.restaurantbookingservice.model.RestaurantTable;
import com.example.restaurantbookingservice.model.TimeSlot;
import com.example.restaurantbookingservice.service.BookingService;
import com.example.restaurantbookingservice.service.RestaurantService;
import com.example.restaurantbookingservice.service.RestaurantTableService;
import com.example.restaurantbookingservice.service.TimeSlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    private BookingService bookingService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private com.example.restaurantbookingservice.repository.RestaurantRepository restaurantRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllBookings() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);
        RestaurantTable table = new RestaurantTable(1, 4, null);
        table.setRestaurant(restaurantRepository.findById(savedRestaurant.getId()).get());
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setTimeSlotId(timeSlot.getId());
        bookingDto.setCustomerName("Customer 1");
        bookingService.addBooking(bookingDto);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"));
    }

    @Test
    @WithMockUser
    public void testGetBookingById() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);
        RestaurantTable table = new RestaurantTable(1, 4, null);
        table.setRestaurant(restaurantRepository.findById(savedRestaurant.getId()).get());
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setTimeSlotId(timeSlot.getId());
        bookingDto.setCustomerName("Customer 1");
        BookingDto savedBooking = bookingService.addBooking(bookingDto);

        mockMvc.perform(get("/bookings/" + savedBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddBooking() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);
        RestaurantTable table = new RestaurantTable(1, 4, null);
        table.setRestaurant(restaurantRepository.findById(savedRestaurant.getId()).get());
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setTimeSlotId(timeSlot.getId());
        bookingDto.setCustomerName("Customer 1");

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteBooking() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);
        RestaurantTable table = new RestaurantTable(1, 4, null);
        table.setRestaurant(restaurantRepository.findById(savedRestaurant.getId()).get());
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setTimeSlotId(timeSlot.getId());
        bookingDto.setCustomerName("Customer 1");
        BookingDto savedBooking = bookingService.addBooking(bookingDto);

        mockMvc.perform(delete("/bookings/" + savedBooking.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteBooking_asUser_isForbidden() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Test Restaurant");
        RestaurantDto savedRestaurant = restaurantService.addRestaurant(restaurantDto);
        RestaurantTable table = new RestaurantTable(1, 4, null);
        table.setRestaurant(restaurantRepository.findById(savedRestaurant.getId()).get());
        restaurantTableService.addTable(table);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2), table);
        timeSlotService.addTimeSlot(timeSlot);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setTimeSlotId(timeSlot.getId());
        bookingDto.setCustomerName("Customer 1");
        BookingDto savedBooking = bookingService.addBooking(bookingDto);

        mockMvc.perform(delete("/bookings/" + savedBooking.getId()))
                .andExpect(status().isForbidden());
    }
}
