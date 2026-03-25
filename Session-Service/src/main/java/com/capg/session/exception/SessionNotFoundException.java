package com.capg.session.exception;

public class SessionNotFoundException extends RuntimeException {
    
    private int sessionId;
    
    public SessionNotFoundException(String message) {
        super(message);
    }
    
    public SessionNotFoundException(String message, int sessionId) {
        super(message);
        this.sessionId = sessionId;
    }
    
    public int getSessionId() {
        return sessionId;
    }
}
