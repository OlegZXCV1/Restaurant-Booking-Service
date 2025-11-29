package com.example.restaurantbookingservice.mapper;

import com.example.restaurantbookingservice.dto.BookingDto;
import com.example.restaurantbookingservice.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "timeSlot.id", target = "timeSlotId")
    BookingDto toDto(Booking booking);
    @Mapping(source = "timeSlotId", target = "timeSlot.id")
    Booking toEntity(BookingDto bookingDto);
}
