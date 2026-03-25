package com.capg.session.bean;

import java.util.Date;

public class SessionDTO {

    private int mentor_id;
    private int learner_id;
    private Date session_Date;

    public int getMentor_id() { return mentor_id; }
    public void setMentor_id(int mentor_id) { this.mentor_id = mentor_id; }

    public int getLearner_id() { return learner_id; }
    public void setLearner_id(int learner_id) { this.learner_id = learner_id; }

    public Date getSession_Date() { return session_Date; }
    public void setSession_Date(Date session_Date) { this.session_Date = session_Date; }

}