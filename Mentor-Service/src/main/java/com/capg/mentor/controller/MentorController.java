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

/**
 * Mentor Controller
 * Handles mentor-related REST endpoints
 * 
 * Exception Handling:
 * - ResourceNotFoundException: Thrown when mentor, user, or skill is not found (HTTP 404)
 * - BadRequestException: Thrown when user has already applied or availability times are invalid (HTTP 400)
 * - AccessDeniedException: Thrown when user lacks required role (HTTP 403)
 */
@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    /**
     * Apply for a mentorship role
     * 
     * @param request Mentor application details
     * @return ApiResponse containing MentorResponse
     * @throws BadRequestException if user already applied
     * @throws ResourceNotFoundException if user or skills do not exist
     */
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

    /**
     * Retrieve all approved mentors
     * 
     * @return ApiResponse containing list of MentorResponse objects
     */
    @GetMapping("/public")
    public ApiResponse<List<MentorResponse>> getAllMentors() {

        List<MentorResponse> mentors = mentorService.getAllMentors();

        return ApiResponse.<List<MentorResponse>>builder()
                .success(true)
                .message("Mentors fetched successfully")
                .data(mentors)
                .build();
    }

    /**
     * Retrieve a specific mentor by ID
     * 
     * @param id Mentor ID
     * @return ApiResponse containing MentorResponse
     * @throws ResourceNotFoundException if mentor is not found
     */
    @GetMapping("/public/{id}")
    public ApiResponse<MentorResponse> getMentorById(@PathVariable Long id) {

        MentorResponse mentor = mentorService.getMentorById(id);

        return ApiResponse.<MentorResponse>builder()
                .success(true)
                .message("Mentor fetched successfully")
                .data(mentor)
                .build();
    }

    /**
     * Add availability slot for a mentor
     * 
     * @param id Mentor ID
     * @param request Availability limits and details
     * @return ApiResponse indicating success
     * @throws ResourceNotFoundException if mentor is not found
     * @throws BadRequestException if start time is after end time
     */
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

    /**
     * Approve a mentor application (Admin only)
     * 
     * @param id Mentor ID
     * @return ApiResponse containing ApprovedMentorResponse
     * @throws ResourceNotFoundException if mentor is not found
     */
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

    /**
     * Update the rating of a mentor
     * 
     * @param mentorId Mentor ID
     * @param rating New rating value
     * @throws ResourceNotFoundException if mentor is not found
     */
    @PutMapping("/{mentorId}/rating")
    public void updateRating(@PathVariable Long mentorId,
                                       @RequestParam Double rating) {
        mentorService.updateRating(mentorId, rating);
    }


}