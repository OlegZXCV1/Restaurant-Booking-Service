package com.example.restaurantbookingservice.service;

import com.example.restaurantbookingservice.dto.BookingDto;
import com.example.restaurantbookingservice.mapper.BookingMapper;
import com.example.restaurantbookingservice.model.Booking;
import com.example.restaurantbookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookingDto getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDto)
                .orElse(null);
    }

    public List<BookingDto> getBookingsByTimeSlotId(Long timeSlotId) {
        return bookingRepository.findByTimeSlotId(timeSlotId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookingDto addBooking(BookingDto bookingDto) {
        List<Booking> bookings = bookingRepository.findByTimeSlotId(bookingDto.getTimeSlotId());
        if (bookings.isEmpty()) {
            Booking booking = bookingMapper.toEntity(bookingDto);
            return bookingMapper.toDto(bookingRepository.save(booking));
        }
        return null;
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
