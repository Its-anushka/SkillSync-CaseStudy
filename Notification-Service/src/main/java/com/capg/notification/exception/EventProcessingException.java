package com.capg.notification.exception;

public class EventProcessingException extends RuntimeException {
    
    private Long eventId;
    
    public EventProcessingException(String message) {
        super(message);
    }
    
    public EventProcessingException(String message, Long eventId) {
        super(message);
        this.eventId = eventId;
    }
    
    public EventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EventProcessingException(String message, Long eventId, Throwable cause) {
        super(message, cause);
        this.eventId = eventId;
    }
    
    public Long getEventId() {
        return eventId;
    }
}
