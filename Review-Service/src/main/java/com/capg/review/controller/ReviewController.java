package com.capg.review.controller;

import com.capg.review.dto.request.ReviewRequest;
import com.capg.review.dto.response.ApiResponse;
import com.capg.review.dto.response.ReviewResponse;
import com.capg.review.service.ReviewService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller responsible for handling all incoming HTTP 
 * Review requests. Operates exclusively within the /reviews 
 * domain boundary.
 */
@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Submits a fresh mentorship review. Evaluates request body semantics, 
     * calls the underlying ReviewService, and uniformly packages the response.
     *
     * @param request the validated ReviewRequest DTO; must not be null
     * @return an ApiResponse uniformly enveloping the persisted ReviewResponse mapping
     * @throws IllegalArgumentException if the provided DTO fails semantic validation
     */

    @PostMapping
    public ApiResponse<ReviewResponse> addReview(
            @Valid @RequestBody final ReviewRequest request) {

        log.info("Received request to append review logic for mentor ID: {}", request.getMentorId());
        var response = reviewService.addReview(request);

        return ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review added successfully")
                .data(response)
                .build();
    }

    /**
     * Retrieves all cached reviews allocated generically under a specific Mentor.
     *
     * @param mentorId the target unique mentor ID
     * @return an ApiResponse uniformly enveloping a fully hydrated List of ReviewResponse configurations
     */
    @GetMapping("/mentor/{mentorId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByMentor(
            @PathVariable final Long mentorId) {

        log.debug("Initializing review pull targeted for mentor ID: {}", mentorId);
        var reviews = reviewService.getReviewsByMentor(mentorId);

        return ApiResponse.<List<ReviewResponse>>builder()
                .success(true)
                .message("Reviews fetched successfully")
                .data(reviews)
                .build();
    }
}