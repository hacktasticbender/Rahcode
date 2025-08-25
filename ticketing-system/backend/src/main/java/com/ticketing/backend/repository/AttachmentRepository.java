package com.ticketing.backend.repository;

import com.ticketing.backend.entity.Attachment;
import com.ticketing.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    
    List<Attachment> findByTicket(Ticket ticket);
    
    List<Attachment> findByTicketId(Long ticketId);
}