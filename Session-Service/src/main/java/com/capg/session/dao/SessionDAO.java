package com.capg.session.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.capg.session.bean.Session;
import com.capg.session.exception.SessionNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class SessionDAO {
	
	@PersistenceContext
	EntityManager em;
	
	public Session requestSession(int mentor_id, int learner_id, Date session_date) {
		Date current_date = new Date();
		Session session = new Session(mentor_id,learner_id,session_date,"REQUESTED",current_date);
		em.persist(session);
		return session;
	}
	
	public Session bookSession(int mentor_id, int learner_id, Date session_date) {
		Date current_date = new Date();
		Session session = new Session(mentor_id,learner_id,session_date,"BOOKED",current_date);
		em.persist(session);
		return session;
	}
	
	public Session acceptSession(int id) {
		Session session = em.find(Session.class, id);
		if (session == null) {
			throw new SessionNotFoundException("Session with id " + id + " not found", id);
		}
		session.setStatus("ACCEPTED");
		session = em.merge(session);
		return session;
	}
	
	public Session rejectSession(int id) {
		Session session = em.find(Session.class, id);
		if (session == null) {
			throw new SessionNotFoundException("Session with id " + id + " not found", id);
		}
		session.setStatus("REJECTED");
		session = em.merge(session);
		return session;
	}
	
	public Session cancelSession(int id) {
		Session session = em.find(Session.class, id);
		if (session == null) {
			throw new SessionNotFoundException("Session with id " + id + " not found", id);
		}
		session.setStatus("CANCELLED");
		session = em.merge(session);
		return session;
	}
	
	public Session completeSession(int id) {
		Session session = em.find(Session.class, id);
		if (session == null) {
			throw new SessionNotFoundException("Session with id " + id + " not found", id);
		}
		session.setStatus("COMPLETED");
		session = em.merge(session);
		return session;
	}

	public java.util.List<Session> getSessionsByUserId(int userId) {
		return em.createQuery("SELECT s FROM Session s WHERE s.learner_id = :userId OR s.mentor_id = :userId", Session.class)
				.setParameter("userId", userId)
				.getResultList();
	}

	public Session getSessionById(int id) {
		Session session = em.find(Session.class, id);
		if (session == null) {
			throw new SessionNotFoundException("Session with id " + id + " not found", id);
		}
		return session;
	}
}
