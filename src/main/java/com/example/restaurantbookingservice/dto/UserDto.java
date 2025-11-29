package com.example.restaurantbookingservice.dto;

import com.example.restaurantbookingservice.model.Role;
import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Set<Role> roles;
}
