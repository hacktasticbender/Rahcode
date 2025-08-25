package com.ticketing.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    
    private Long id;
    private String content;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}