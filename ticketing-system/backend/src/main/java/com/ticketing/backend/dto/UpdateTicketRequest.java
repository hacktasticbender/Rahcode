package com.ticketing.backend.dto;

import com.ticketing.backend.entity.Priority;
import com.ticketing.backend.entity.Status;
import lombok.Data;

@Data
public class UpdateTicketRequest {
    
    private String subject;
    private String description;
    private Priority priority;
    private Status status;
    private Long assigneeId;
}