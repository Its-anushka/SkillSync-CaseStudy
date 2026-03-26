package com.capg.mentor.service;

import com.capg.mentor.dto.request.AvailabilityRequest;
import com.capg.mentor.dto.request.MentorRequest;
import com.capg.mentor.dto.response.ApiResponse;
import com.capg.mentor.dto.response.ApprovedMentorResponse;
import com.capg.mentor.dto.response.MentorResponse;

import java.util.List;

public interface MentorService {

    MentorResponse applyForMentor(MentorRequest request);

    List<MentorResponse> getAllMentors();

    MentorResponse getMentorById(Long id);

    void addAvailability(AvailabilityRequest request);

    ApprovedMentorResponse approveMentor(Long id);

    void updateRating(Long mentorId, Double rating);
}