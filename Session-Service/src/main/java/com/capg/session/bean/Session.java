package com.capg.session.bean;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="sessions")
public class Session {
	
	@Id
	@GeneratedValue
	private int id;
	
	private int mentor_id;
	private int learner_id;
	
	Date session_Date;
	String status;
	Date created_at;
	
	public Session() {
	}
	
	public Session(int mentor_id,int learner_id, Date session_Date,String status,Date created_at){
		this.mentor_id = mentor_id;
		this.learner_id = learner_id;
		this.session_Date = session_Date;
		this.status = status;
		this.created_at = created_at;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMentor_id() {
		return mentor_id;
	}
	public void setMentor_id(int mentor_id) {
		this.mentor_id = mentor_id;
	}
	public int getLearner_id() {
		return learner_id;
	}
	public void setLearner_id(int learner_id) {
		this.learner_id = learner_id;
	}
	public Date getSession_Date() {
		return session_Date;
	}
	public void setSession_Date(Date session_Date) {
		this.session_Date = session_Date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
