package com.capg.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.capg.notification.event.SessionEvent;
import com.capg.notification.exception.EventProcessingException;

/**
 * Notification Service
 * Handles processing of session events and sending notifications
 */
@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Handle session events and send appropriate notifications
     * 
     * @param event the SessionEvent to process
     * @throws EventProcessingException if event processing fails
     */
    public void handle(SessionEvent event) {
        
        if (event == null) {
            throw new EventProcessingException("Event cannot be null");
        }
        
        if (event.getSessionId() == null || event.getSessionId() <= 0) {
            throw new EventProcessingException("Invalid session ID in event", event.getSessionId());
        }
        
        try {
            String type = event.getEventType();
            
            if (type == null || type.isEmpty()) {
                throw new EventProcessingException("Event type cannot be null or empty", event.getSessionId());
            }
            
            switch (type) {
                case "REQUESTED":
                    handleSessionRequestedNotification(event);
                    break;
                case "ACCEPTED":
                    handleSessionAcceptedNotification(event);
                    break;
                case "REMINDER":
                    handleSessionReminderNotification(event);
                    break;
                case "CANCELLED":
                    handleSessionCancelledNotification(event);
                    break;
                case "REJECTED":
                    handleSessionRejectedNotification(event);
                    break;
                default:
                    logger.warn("Unknown event type: {} for session ID: {}", type, event.getSessionId());
            }
        } catch (EventProcessingException e) {
            logger.error("Error processing event for session ID: {}", event.getSessionId(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing event for session ID: {}", event.getSessionId(), e);
            throw new EventProcessingException("Failed to process event: " + e.getMessage(), 
                    event.getSessionId(), e);
        }
    }
    
    private void handleSessionRequestedNotification(SessionEvent event) {
        System.out.println("Notification: Session Booked " + event.getSessionId());
        logger.info("Session requested notification sent for session ID: {}", event.getSessionId());
    }
    
    private void handleSessionAcceptedNotification(SessionEvent event) {
        System.out.println("Notification: Session Accepted " + event.getSessionId());
        logger.info("Session accepted notification sent for session ID: {}", event.getSessionId());
    }
    
    private void handleSessionReminderNotification(SessionEvent event) {
        System.out.println("Notification: Session Reminder " + event.getSessionId());
        logger.info("Session reminder notification sent for session ID: {}", event.getSessionId());
    }
    
    private void handleSessionCancelledNotification(SessionEvent event) {
        System.out.println("Notification: Session Cancelled " + event.getSessionId());
        logger.info("Session cancelled notification sent for session ID: {}", event.getSessionId());
    }
    
    private void handleSessionRejectedNotification(SessionEvent event) {
        System.out.println("Notification: Session Rejected " + event.getSessionId());
        logger.info("Session rejected notification sent for session ID: {}", event.getSessionId());
    }
}