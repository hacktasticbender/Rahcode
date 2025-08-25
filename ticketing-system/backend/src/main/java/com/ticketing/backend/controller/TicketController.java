package com.ticketing.backend.controller;

import com.ticketing.backend.dto.*;
import com.ticketing.backend.entity.Priority;
import com.ticketing.backend.entity.Status;
import com.ticketing.backend.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<TicketDto>> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketDto ticket = ticketService.createTicket(request);
        return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", ticket));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDto>> getTicket(@PathVariable Long id) {
        TicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TicketDto>>> getTickets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.getTickets(search, status, priority, assigneeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
    
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<TicketDto>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.getMyTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
    
    @GetMapping("/assigned")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<TicketDto>>> getAssignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.getAssignedTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDto>> updateTicket(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateTicketRequest request) {
        TicketDto ticket = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(ApiResponse.success("Ticket updated successfully", ticket));
    }
    
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TicketDto>> assignTicket(
            @PathVariable Long id, 
            @RequestParam Long assigneeId) {
        TicketDto ticket = ticketService.assignTicket(id, assigneeId);
        return ResponseEntity.ok(ApiResponse.success("Ticket assigned successfully", ticket));
    }
    
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> addComment(
            @PathVariable Long id, 
            @Valid @RequestBody CreateCommentRequest request) {
        CommentDto comment = ticketService.addComment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Comment added successfully", comment));
    }
    
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getTicketComments(@PathVariable Long id) {
        List<CommentDto> comments = ticketService.getTicketComments(id);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    @PostMapping("/{id}/rating")
    public ResponseEntity<ApiResponse<RatingDto>> rateTicket(
            @PathVariable Long id, 
            @Valid @RequestBody CreateRatingRequest request) {
        RatingDto rating = ticketService.rateTicket(id, request);
        return ResponseEntity.ok(ApiResponse.success("Ticket rated successfully", rating));
    }
}