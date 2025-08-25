package com.ticketing.backend.repository;

import com.ticketing.backend.entity.Priority;
import com.ticketing.backend.entity.Status;
import com.ticketing.backend.entity.Ticket;
import com.ticketing.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByRequester(User requester);
    
    List<Ticket> findByAssignee(User assignee);
    
    List<Ticket> findByStatus(Status status);
    
    List<Ticket> findByPriority(Priority priority);
    
    Page<Ticket> findByRequester(User requester, Pageable pageable);
    
    Page<Ticket> findByAssignee(User assignee, Pageable pageable);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:searchTerm IS NULL OR LOWER(t.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
           "(:requesterId IS NULL OR t.requester.id = :requesterId)")
    Page<Ticket> findTicketsWithFilters(@Param("searchTerm") String searchTerm,
                                       @Param("status") Status status,
                                       @Param("priority") Priority priority,
                                       @Param("assigneeId") Long assigneeId,
                                       @Param("requesterId") Long requesterId,
                                       Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") Status status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignee = :assignee AND t.status IN :statuses")
    long countByAssigneeAndStatusIn(@Param("assignee") User assignee, @Param("statuses") List<Status> statuses);
}