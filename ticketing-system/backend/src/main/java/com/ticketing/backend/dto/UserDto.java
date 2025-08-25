package com.ticketing.backend.dto;

import com.ticketing.backend.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}