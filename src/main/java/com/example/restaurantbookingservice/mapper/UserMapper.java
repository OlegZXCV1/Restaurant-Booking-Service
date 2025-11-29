package com.example.restaurantbookingservice.mapper;

import com.example.restaurantbookingservice.dto.UserDto;
import com.example.restaurantbookingservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
