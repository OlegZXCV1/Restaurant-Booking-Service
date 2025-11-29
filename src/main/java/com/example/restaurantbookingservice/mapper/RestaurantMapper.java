package com.example.restaurantbookingservice.mapper;

import com.example.restaurantbookingservice.dto.RestaurantDto;
import com.example.restaurantbookingservice.model.Restaurant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    RestaurantDto toDto(Restaurant restaurant);
    Restaurant toEntity(RestaurantDto restaurantDto);
}
