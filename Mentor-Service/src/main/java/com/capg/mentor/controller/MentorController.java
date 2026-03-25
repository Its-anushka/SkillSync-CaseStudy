package com.capg.mentor.controller;

import com.capg.mentor.dto.request.AvailabilityRequest;
import com.capg.mentor.dto.request.MentorRequest;
import com.capg.mentor.dto.response.ApiResponse;
import com.capg.mentor.dto.response.ApprovedMentorResponse;
import com.capg.mentor.dto.response.MentorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.capg.mentor.service.MentorService;

import java.util.List;

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    //Apply for mentor → ONLY LEARNER
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/apply")
    public ApiResponse<MentorResponse> applyForMentor(
            @Valid @RequestBody MentorRequest request) {

        // Inject logged-in user email from JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        request.setEmail(email);

        MentorResponse response = mentorService.applyForMentor(request);

        return ApiResponse.<MentorResponse>builder()
                .success(true)
                .message("Mentor application submitted successfully")
                .data(response)
                .build();
    }

    //Public access
    @GetMapping("/public")
    public ApiResponse<List<MentorResponse>> getAllMentors() {

        List<MentorResponse> mentors = mentorService.getAllMentors();

        return ApiResponse.<List<MentorResponse>>builder()
                .success(true)
                .message("Mentors fetched successfully")
                .data(mentors)
                .build();
    }

    //Public access
    @GetMapping("/public/{id}")
    public ApiResponse<MentorResponse> getMentorById(@PathVariable Long id) {

        MentorResponse mentor = mentorService.getMentorById(id);

        return ApiResponse.<MentorResponse>builder()
                .success(true)
                .message("Mentor fetched successfully")
                .data(mentor)
                .build();
    }

    //Only mentor can add availability + ownership check
    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{id}/availability")
    public ApiResponse<Void> addAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Pass email for ownership validation
        request.setMentorId(id);
        request.setEmail(email);

        mentorService.addAvailability(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Availability added successfully")
                .data(null)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ApiResponse<ApprovedMentorResponse> approveMentor(@PathVariable Long id){
        ApprovedMentorResponse mentor = mentorService.approveMentor(id);

        return ApiResponse.<ApprovedMentorResponse>builder()
                .success(true)
                .message("Mentor approved!")
                .data(mentor)
                .build();
    }

}