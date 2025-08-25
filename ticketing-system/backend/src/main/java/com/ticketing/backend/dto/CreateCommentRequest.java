package com.ticketing.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    
    @NotBlank
    private String content;
}