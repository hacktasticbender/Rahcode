package com.ticketing.backend.dto;

import com.ticketing.backend.entity.Priority;
import com.ticketing.backend.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketDto {
    
    private Long id;
    private String subject;
    private String description;
    private Priority priority;
    private Status status;
    private UserDto requester;
    private UserDto assignee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private List<CommentDto> comments;
    private List<AttachmentDto> attachments;
    private RatingDto rating;
}