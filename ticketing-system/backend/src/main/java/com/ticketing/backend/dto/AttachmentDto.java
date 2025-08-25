package com.ticketing.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentDto {
    
    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private UserDto uploadedBy;
    private LocalDateTime uploadedAt;
}