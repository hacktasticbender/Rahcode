package com.ticketing.backend.repository;

import com.ticketing.backend.entity.Rating;
import com.ticketing.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    Optional<Rating> findByTicket(Ticket ticket);
    
    Optional<Rating> findByTicketId(Long ticketId);
    
    boolean existsByTicketId(Long ticketId);
}