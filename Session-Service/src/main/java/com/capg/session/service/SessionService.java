package com.capg.session.service;

import java.util.Date;

import com.capg.session.dto.ApiResponse;
import com.capg.session.dto.MentorResponse;
import com.capg.session.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capg.session.bean.Session;
import com.capg.session.dao.SessionDAO;
import com.capg.session.event.SessionEvent;
import com.capg.session.event.SessionEventPublisher;
import com.capg.session.exception.InvalidSessionException;
import com.capg.session.client.MentorServiceClient;
import com.capg.session.client.UserServiceClient;
import feign.FeignException;

/**
 * Session Service
 * Handles business logic for session-related operations
 * 
 * Exception Handling:
 * - InvalidSessionException: Thrown when session data is invalid, dates are incorrect, or session state transitions are not allowed (HTTP 400)
 * - FeignException: Intercepted and wrapped as InvalidSessionException when mentor/user validation fails via remote clients
 */
@Service
public class SessionService {
	
	@Autowired
	SessionDAO dao;
	
	@Autowired
    SessionEventPublisher publisher;

	@Autowired
	UserServiceClient userServiceClient;

	@Autowired
	MentorServiceClient mentorServiceClient;
	
	/**
	 * Request a new session between mentor and learner
	 * 
	 * @param mentor_id Mentor ID
	 * @param learner_id Learner ID
	 * @param session_date Session Date
	 * @return Session representing the requested session
	 * @throws InvalidSessionException if IDs are invalid, date is in past, or user/mentor does not exist
	 */
	public Session requestSessionService(int mentor_id, int learner_id, Date session_date) {
		
		if (mentor_id <= 0 || learner_id <= 0) {
			throw new InvalidSessionException("Mentor ID and Learner ID must be valid positive integers");
		}
		
		if (session_date == null || session_date.before(new Date())) {
			throw new InvalidSessionException("Session date cannot be null or in the past");
		}
		
		try {
			// Validate listener/user profile is valid
			UserDto user = userServiceClient.getUserById((long) learner_id);
		} catch (FeignException e) {
			throw new InvalidSessionException("Learner ID is invalid or user does not exist");
		}

		try {
			// Validate mentor profile is valid
			ApiResponse<MentorResponse> mentor = mentorServiceClient.getMentorById((long) mentor_id);
			if (mentor == null || mentor.getData() == null) {
				throw new InvalidSessionException("Mentor does not exist");
			}
		} catch (FeignException e) {
			throw new InvalidSessionException("Mentor ID is invalid or mentor does not exist");
		}

		Session session =  dao.requestSession(mentor_id, learner_id, session_date);
		
		SessionEvent event = new SessionEvent();
	    event.setEventType("REQUESTED");
		event.setSessionId((long)session.getId());

	    publisher.publish(event);
		
		return session;
	}
	
	/**
	 * Book a new session
	 * 
	 * @param mentor_id Mentor ID
	 * @param learner_id Learner ID
	 * @param session_date Session Date
	 * @return Session representing the booked session
	 * @throws InvalidSessionException if IDs are invalid, date is in past, or user/mentor does not exist
	 */
	public Session bookSessionService(int mentor_id, int learner_id, Date session_date) {
		
		if (mentor_id <= 0 || learner_id <= 0) {
			throw new InvalidSessionException("Mentor ID and Learner ID must be valid positive integers");
		}
		
		if (session_date == null || session_date.before(new Date())) {
			throw new InvalidSessionException("Session date cannot be null or in the past");
		}
		
		try {
			// Validate listener/user profile is valid
			UserDto user = userServiceClient.getUserById((long) learner_id);
		} catch (FeignException e) {
			throw new InvalidSessionException("Learner ID is invalid or user does not exist");
		}

		try {
			// Validate mentor profile is valid
			ApiResponse<MentorResponse> mentor = mentorServiceClient.getMentorById((long) mentor_id);
			if (mentor == null || mentor.getData() == null) {
				throw new InvalidSessionException("Mentor does not exist");
			}
		} catch (FeignException e) {
			throw new InvalidSessionException("Mentor ID is invalid or mentor does not exist");
		}

		Session session =  dao.bookSession(mentor_id, learner_id, session_date);
		
		SessionEvent event = new SessionEvent();
	    event.setEventType("BOOKED");
		event.setSessionId((long)session.getId());

	    publisher.publish(event);
		
		return session;
	}
	
	/**
	 * Accept a session request
	 * 
	 * @param id Session ID
	 * @return Session representing the accepted session
	 * @throws InvalidSessionException if session ID is invalid
	 */
	public Session acceptSessionService(int id) {
		
		if (id <= 0) {
			throw new InvalidSessionException("Session ID must be a valid positive integer");
		}
		
		Session session = dao.acceptSession(id);
		
		SessionEvent event = new SessionEvent();
	    event.setEventType("ACCEPTED");
	    event.setSessionId((long)id);

	    publisher.publish(event);
	    
		return session;
	}
	
	/**
	 * Reject a session request
	 * 
	 * @param id Session ID
	 * @return Session representing the rejected session
	 * @throws InvalidSessionException if session ID is invalid
	 */
	public Session rejectSessionService(int id) {
		
		if (id <= 0) {
			throw new InvalidSessionException("Session ID must be a valid positive integer");
		}
		
		Session session = dao.rejectSession(id);
		
		SessionEvent event = new SessionEvent();
	    event.setEventType("REJECTED");
	    event.setSessionId((long)id);

	    publisher.publish(event);
		
		return session;
	}
	
	/**
	 * Cancel a session
	 * 
	 * @param id Session ID
	 * @return Session representing the cancelled session
	 * @throws InvalidSessionException if session ID is invalid
	 */
	public Session cancelSessionService(int id) {
		
		if (id <= 0) {
			throw new InvalidSessionException("Session ID must be a valid positive integer");
		}
		
		Session session = dao.cancelSession(id);
		
		SessionEvent event = new SessionEvent();
	    event.setEventType("CANCELLED");
	    event.setSessionId((long)id);

	    publisher.publish(event);
		
		return session;
	}
	
	/**
	 * Complete a session
	 * 
	 * @param id Session ID
	 * @return Session representing the completed session
	 * @throws InvalidSessionException if session ID is invalid
	 */
	public Session completeSessionService(int id) {
		
		if (id <= 0) {
			throw new InvalidSessionException("Session ID must be a valid positive integer");
		}
		
		Session session = dao.completeSession(id);
		
		SessionEvent event = new SessionEvent();
	    event.setEventType("COMPLETED");
	    event.setSessionId((long)id);

	    publisher.publish(event);
		
		return session;
	}

	/**
	 * Retrieve all sessions for a user
	 * 
	 * @param userId User ID
	 * @return List of Session objects
	 */
	public java.util.List<Session> getSessionsByUser(int userId) {
		return dao.getSessionsByUserId(userId);
	}

	/**
	 * Retrieve my sessions
	 * 
	 * @param userId User ID
	 * @return List of Session objects
	 */
	public java.util.List<Session> getMySessionService(int userId) {
		return dao.getSessionsByUserId(userId);
	}

	/**
	 * Retrieve a session by its ID
	 * 
	 * @param id Session ID
	 * @return Session object
	 */
	public Session getSessionByIdService(int id) {
		return dao.getSessionById(id);
	}
}
