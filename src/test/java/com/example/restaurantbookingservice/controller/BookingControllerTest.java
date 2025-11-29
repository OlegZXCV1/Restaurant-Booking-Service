package com.example.restaurantbookingservice.controller;

import com.example.restaurantbookingservice.config.SecurityConfig;
import com.example.restaurantbookingservice.dto.BookingDto;
import com.example.restaurantbookingservice.security.JwtAuthenticationEntryPoint;
import com.example.restaurantbookingservice.security.JwtRequestFilter;
import com.example.restaurantbookingservice.security.JwtTokenProvider;
import com.example.restaurantbookingservice.service.BookingService;
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

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

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
    @WithMockUser(roles = "ADMIN")
    void getAllBookings() throws Exception {
        BookingDto booking1 = new BookingDto();
        booking1.setCustomerName("Customer 1");
        BookingDto booking2 = new BookingDto();
        booking2.setCustomerName("Customer 2");
        List<BookingDto> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"))
                .andExpect(jsonPath("$[1].customerName").value("Customer 2"));
    }

    @Test
    @WithMockUser
    void getBookingById() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setCustomerName("Customer 1");
        when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    @WithMockUser
    void getBookingsByTimeSlotId() throws Exception {
        BookingDto booking1 = new BookingDto();
        booking1.setCustomerName("Customer 1");
        BookingDto booking2 = new BookingDto();
        booking2.setCustomerName("Customer 2");
        List<BookingDto> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getBookingsByTimeSlotId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/timeslot/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addBooking() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setCustomerName("Customer 1");
        when(bookingService.addBooking(any(BookingDto.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void addBooking_whenTimeSlotIsTaken() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setCustomerName("Customer 1");
        when(bookingService.addBooking(any(BookingDto.class))).thenReturn(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBooking() throws Exception {
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllBookings_asUser_isForbidden() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBooking_asUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isForbidden());
    }
}
