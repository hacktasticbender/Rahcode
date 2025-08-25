package com.ticketing.backend.dto;

import com.ticketing.backend.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTicketRequest {
    
    @NotBlank
    private String subject;
    
    @NotBlank
    private String description;
    
    private Priority priority = Priority.MEDIUM;
}