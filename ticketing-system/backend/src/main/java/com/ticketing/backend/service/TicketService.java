package com.ticketing.backend.service;

import com.ticketing.backend.dto.*;
import com.ticketing.backend.entity.*;
import com.ticketing.backend.exception.AccessDeniedException;
import com.ticketing.backend.exception.ResourceNotFoundException;
import com.ticketing.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private EmailService emailService;
    
    public TicketDto createTicket(CreateTicketRequest request) {
        User currentUser = getCurrentUser();
        
        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setRequester(currentUser);
        ticket.setStatus(Status.OPEN);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Send email notification to admins/support agents
        emailService.sendTicketCreatedNotification(savedTicket);
        
        return convertToDto(savedTicket);
    }
    
    public TicketDto getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        
        validateTicketAccess(ticket);
        return convertToDto(ticket);
    }
    
    public Page<TicketDto> getTickets(String searchTerm, Status status, Priority priority, 
                                     Long assigneeId, Pageable pageable) {
        User currentUser = getCurrentUser();
        Long requesterId = null;
        
        // Regular users can only see their own tickets
        if (currentUser.getRole() == Role.USER) {
            requesterId = currentUser.getId();
            assigneeId = null; // Users can't filter by assignee
        }
        
        Page<Ticket> tickets = ticketRepository.findTicketsWithFilters(
                searchTerm, status, priority, assigneeId, requesterId, pageable);
        
        return tickets.map(this::convertToDto);
    }
    
    public Page<TicketDto> getMyTickets(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Ticket> tickets = ticketRepository.findByRequester(currentUser, pageable);
        return tickets.map(this::convertToDto);
    }
    
    public Page<TicketDto> getAssignedTickets(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Ticket> tickets = ticketRepository.findByAssignee(currentUser, pageable);
        return tickets.map(this::convertToDto);
    }
    
    public TicketDto updateTicket(Long ticketId, UpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        
        validateTicketUpdateAccess(ticket);
        
        boolean statusChanged = false;
        Status oldStatus = ticket.getStatus();
        
        if (request.getSubject() != null) {
            ticket.setSubject(request.getSubject());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
            statusChanged = !oldStatus.equals(request.getStatus());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssigneeId()));
            ticket.setAssignee(assignee);
        }
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Send email notifications for status changes
        if (statusChanged) {
            emailService.sendTicketStatusChangedNotification(updatedTicket, oldStatus);
        }
        
        return convertToDto(updatedTicket);
    }
    
    public TicketDto assignTicket(Long ticketId, Long assigneeId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + assigneeId));
        
        validateTicketAssignAccess(ticket);
        
        User oldAssignee = ticket.getAssignee();
        ticket.setAssignee(assignee);
        
        if (ticket.getStatus() == Status.OPEN) {
            ticket.setStatus(Status.IN_PROGRESS);
        }
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Send email notification for assignment
        emailService.sendTicketAssignedNotification(updatedTicket, oldAssignee);
        
        return convertToDto(updatedTicket);
    }
    
    public CommentDto addComment(Long ticketId, CreateCommentRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        
        validateTicketAccess(ticket);
        
        User currentUser = getCurrentUser();
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTicket(ticket);
        comment.setUser(currentUser);
        
        Comment savedComment = commentRepository.save(comment);
        
        // Send email notification for new comment
        emailService.sendTicketCommentNotification(ticket, savedComment);
        
        return convertCommentToDto(savedComment);
    }
    
    public List<CommentDto> getTicketComments(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        
        validateTicketAccess(ticket);
        
        List<Comment> comments = commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
        return comments.stream().map(this::convertCommentToDto).collect(Collectors.toList());
    }
    
    public RatingDto rateTicket(Long ticketId, CreateRatingRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        
        User currentUser = getCurrentUser();
        
        // Only the ticket requester can rate the ticket
        if (!ticket.getRequester().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the ticket requester can rate the ticket");
        }
        
        // Ticket must be resolved or closed to be rated
        if (ticket.getStatus() != Status.RESOLVED && ticket.getStatus() != Status.CLOSED) {
            throw new AccessDeniedException("Ticket must be resolved or closed to be rated");
        }
        
        // Check if already rated
        if (ratingRepository.existsByTicketId(ticketId)) {
            throw new AccessDeniedException("Ticket has already been rated");
        }
        
        Rating rating = new Rating();
        rating.setStars(request.getStars());
        rating.setFeedback(request.getFeedback());
        rating.setTicket(ticket);
        rating.setUser(currentUser);
        
        Rating savedRating = ratingRepository.save(rating);
        
        return convertRatingToDto(savedRating);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
    
    private void validateTicketAccess(Ticket ticket) {
        User currentUser = getCurrentUser();
        
        // Admins and support agents can access all tickets
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SUPPORT_AGENT) {
            return;
        }
        
        // Regular users can only access their own tickets or tickets assigned to them
        if (!ticket.getRequester().getId().equals(currentUser.getId()) && 
            (ticket.getAssignee() == null || !ticket.getAssignee().getId().equals(currentUser.getId()))) {
            throw new AccessDeniedException("You don't have access to this ticket");
        }
    }
    
    private void validateTicketUpdateAccess(Ticket ticket) {
        User currentUser = getCurrentUser();
        
        // Admins can update any ticket
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        
        // Support agents can update assigned tickets or any ticket for status/assignment changes
        if (currentUser.getRole() == Role.SUPPORT_AGENT) {
            return;
        }
        
        // Regular users can only update their own tickets (subject, description, priority)
        if (!ticket.getRequester().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own tickets");
        }
    }
    
    private void validateTicketAssignAccess(Ticket ticket) {
        User currentUser = getCurrentUser();
        
        // Only admins and support agents can assign tickets
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.SUPPORT_AGENT) {
            throw new AccessDeniedException("You don't have permission to assign tickets");
        }
    }
    
    private TicketDto convertToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        dto.setClosedAt(ticket.getClosedAt());
        
        // Convert requester
        dto.setRequester(convertUserToDto(ticket.getRequester()));
        
        // Convert assignee if present
        if (ticket.getAssignee() != null) {
            dto.setAssignee(convertUserToDto(ticket.getAssignee()));
        }
        
        return dto;
    }
    
    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }
    
    private CommentDto convertCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setUser(convertUserToDto(comment.getUser()));
        return dto;
    }
    
    private RatingDto convertRatingToDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setStars(rating.getStars());
        dto.setFeedback(rating.getFeedback());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUser(convertUserToDto(rating.getUser()));
        return dto;
    }
}