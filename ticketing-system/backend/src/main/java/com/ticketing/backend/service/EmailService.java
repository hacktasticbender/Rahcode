package com.ticketing.backend.service;

import com.ticketing.backend.entity.*;
import com.ticketing.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendTicketCreatedNotification(Ticket ticket) {
        try {
            // Notify admins and support agents
            List<User> notificationRecipients = userRepository.findByRole(Role.ADMIN);
            notificationRecipients.addAll(userRepository.findByRole(Role.SUPPORT_AGENT));
            
            for (User recipient : notificationRecipients) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(recipient.getEmail());
                message.setSubject("New Ticket Created - #" + ticket.getId());
                message.setText(String.format(
                    "A new ticket has been created:\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Priority: %s\n" +
                    "Requested by: %s %s (%s)\n" +
                    "Description: %s\n\n" +
                    "Please review and assign the ticket as needed.",
                    ticket.getId(),
                    ticket.getSubject(),
                    ticket.getPriority(),
                    ticket.getRequester().getFirstName(),
                    ticket.getRequester().getLastName(),
                    ticket.getRequester().getEmail(),
                    ticket.getDescription()
                ));
                
                mailSender.send(message);
            }
            
            logger.info("Ticket creation notifications sent for ticket #" + ticket.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send ticket creation notification for ticket #" + ticket.getId(), e);
        }
    }
    
    public void sendTicketAssignedNotification(Ticket ticket, User oldAssignee) {
        try {
            // Notify the new assignee
            if (ticket.getAssignee() != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(ticket.getAssignee().getEmail());
                message.setSubject("Ticket Assigned to You - #" + ticket.getId());
                message.setText(String.format(
                    "You have been assigned to a ticket:\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Priority: %s\n" +
                    "Status: %s\n" +
                    "Requested by: %s %s (%s)\n" +
                    "Description: %s\n\n" +
                    "Please review the ticket and take appropriate action.",
                    ticket.getId(),
                    ticket.getSubject(),
                    ticket.getPriority(),
                    ticket.getStatus(),
                    ticket.getRequester().getFirstName(),
                    ticket.getRequester().getLastName(),
                    ticket.getRequester().getEmail(),
                    ticket.getDescription()
                ));
                
                mailSender.send(message);
            }
            
            // Notify the ticket requester
            SimpleMailMessage requesterMessage = new SimpleMailMessage();
            requesterMessage.setFrom(fromEmail);
            requesterMessage.setTo(ticket.getRequester().getEmail());
            requesterMessage.setSubject("Your Ticket Has Been Assigned - #" + ticket.getId());
            requesterMessage.setText(String.format(
                "Your ticket has been assigned:\n\n" +
                "Ticket ID: #%d\n" +
                "Subject: %s\n" +
                "Assigned to: %s %s\n" +
                "Status: %s\n\n" +
                "You will receive updates as work progresses on your ticket.",
                ticket.getId(),
                ticket.getSubject(),
                ticket.getAssignee() != null ? ticket.getAssignee().getFirstName() : "Unassigned",
                ticket.getAssignee() != null ? ticket.getAssignee().getLastName() : "",
                ticket.getStatus()
            ));
            
            mailSender.send(requesterMessage);
            
            logger.info("Ticket assignment notifications sent for ticket #" + ticket.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send ticket assignment notification for ticket #" + ticket.getId(), e);
        }
    }
    
    public void sendTicketStatusChangedNotification(Ticket ticket, Status oldStatus) {
        try {
            // Notify the ticket requester
            SimpleMailMessage requesterMessage = new SimpleMailMessage();
            requesterMessage.setFrom(fromEmail);
            requesterMessage.setTo(ticket.getRequester().getEmail());
            requesterMessage.setSubject("Ticket Status Updated - #" + ticket.getId());
            requesterMessage.setText(String.format(
                "Your ticket status has been updated:\n\n" +
                "Ticket ID: #%d\n" +
                "Subject: %s\n" +
                "Previous Status: %s\n" +
                "New Status: %s\n" +
                "Assigned to: %s %s\n\n" +
                "Please check the ticket for more details.",
                ticket.getId(),
                ticket.getSubject(),
                oldStatus,
                ticket.getStatus(),
                ticket.getAssignee() != null ? ticket.getAssignee().getFirstName() : "Unassigned",
                ticket.getAssignee() != null ? ticket.getAssignee().getLastName() : ""
            ));
            
            mailSender.send(requesterMessage);
            
            // If ticket is assigned, also notify the assignee
            if (ticket.getAssignee() != null && !ticket.getAssignee().getId().equals(ticket.getRequester().getId())) {
                SimpleMailMessage assigneeMessage = new SimpleMailMessage();
                assigneeMessage.setFrom(fromEmail);
                assigneeMessage.setTo(ticket.getAssignee().getEmail());
                assigneeMessage.setSubject("Assigned Ticket Status Updated - #" + ticket.getId());
                assigneeMessage.setText(String.format(
                    "A ticket assigned to you has been updated:\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Previous Status: %s\n" +
                    "New Status: %s\n" +
                    "Requested by: %s %s (%s)\n\n" +
                    "Please review the updated ticket.",
                    ticket.getId(),
                    ticket.getSubject(),
                    oldStatus,
                    ticket.getStatus(),
                    ticket.getRequester().getFirstName(),
                    ticket.getRequester().getLastName(),
                    ticket.getRequester().getEmail()
                ));
                
                mailSender.send(assigneeMessage);
            }
            
            logger.info("Ticket status change notifications sent for ticket #" + ticket.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send ticket status change notification for ticket #" + ticket.getId(), e);
        }
    }
    
    public void sendTicketCommentNotification(Ticket ticket, Comment comment) {
        try {
            // Notify the ticket requester (if they didn't post the comment)
            if (!comment.getUser().getId().equals(ticket.getRequester().getId())) {
                SimpleMailMessage requesterMessage = new SimpleMailMessage();
                requesterMessage.setFrom(fromEmail);
                requesterMessage.setTo(ticket.getRequester().getEmail());
                requesterMessage.setSubject("New Comment on Your Ticket - #" + ticket.getId());
                requesterMessage.setText(String.format(
                    "A new comment has been added to your ticket:\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Comment by: %s %s\n" +
                    "Comment: %s\n\n" +
                    "Please check the ticket for full details.",
                    ticket.getId(),
                    ticket.getSubject(),
                    comment.getUser().getFirstName(),
                    comment.getUser().getLastName(),
                    comment.getContent()
                ));
                
                mailSender.send(requesterMessage);
            }
            
            // Notify the assignee (if they didn't post the comment and are different from requester)
            if (ticket.getAssignee() != null && 
                !comment.getUser().getId().equals(ticket.getAssignee().getId()) &&
                !ticket.getAssignee().getId().equals(ticket.getRequester().getId())) {
                
                SimpleMailMessage assigneeMessage = new SimpleMailMessage();
                assigneeMessage.setFrom(fromEmail);
                assigneeMessage.setTo(ticket.getAssignee().getEmail());
                assigneeMessage.setSubject("New Comment on Assigned Ticket - #" + ticket.getId());
                assigneeMessage.setText(String.format(
                    "A new comment has been added to a ticket assigned to you:\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Comment by: %s %s\n" +
                    "Comment: %s\n\n" +
                    "Please check the ticket for full details.",
                    ticket.getId(),
                    ticket.getSubject(),
                    comment.getUser().getFirstName(),
                    comment.getUser().getLastName(),
                    comment.getContent()
                ));
                
                mailSender.send(assigneeMessage);
            }
            
            logger.info("Ticket comment notifications sent for ticket #" + ticket.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send ticket comment notification for ticket #" + ticket.getId(), e);
        }
    }
}