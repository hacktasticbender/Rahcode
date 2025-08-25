package com.ticketing.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingDto {
    
    private Long id;
    private Integer stars;
    private String feedback;
    private UserDto user;
    private LocalDateTime createdAt;
}