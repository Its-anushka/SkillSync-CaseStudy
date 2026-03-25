package com.capg.notification.listener;

import com.capg.notification.event.SessionEvent;
import com.capg.notification.exception.EventProcessingException;
import com.capg.notification.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes asynchronous session broadcasts transported from the centralized RabbitMQ broker, 
 * pushing valid events into the lower-level Notification Service.
 */
@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    
    private final NotificationService notificationService;

    public NotificationListener(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Consumer thread attached generically to 'notification.queue'. Safely unpacks 
     * the serialized payload and proxies downstream processing.
     * 
     * @param event the deserialized SessionEvent payload; must not be null
     * @throws EventProcessingException if the payload evaluates strictly empty
     * @throws AmqpRejectAndDontRequeueException if an unrecoverable failure occurs inside downstream processing, routing automatically to DLQ mechanisms
     */
    @RabbitListener(queues = "notification.queue")
    public void consume(final SessionEvent event) {
        if (event == null) {
            log.error("Received null configuration event from target message queue.");
            throw new EventProcessingException("Empty event trapped during asynchronous stream cycle.");
        }

        try {
            log.debug("Initiating asynchronous event handler logic inside Notification Queue mapping for session ID: {}", 
                    event.getSessionId());
            
            notificationService.handle(event);
            
            log.info("Successfully ingested and processed asynchronous broadcast matching Session ID: {}", 
                    event.getSessionId());

        } catch (EventProcessingException exception) {
            log.error("Explicit event parsing threshold failure intercepted from queue: {}", exception.getMessage(), exception);
            throw exception; 
        } catch (RuntimeException runtimeException) {
            log.error("Ambiguous runtime fault intercepted during ingestion footprint: {}", runtimeException.getMessage(), runtimeException);
            throw new AmqpRejectAndDontRequeueException("Routing failed operation payload systematically towards dead letter queue.", runtimeException);
        }
    }
}