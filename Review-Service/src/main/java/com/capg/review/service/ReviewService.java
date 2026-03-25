package com.capg.review.service;

import com.capg.review.dto.request.ReviewRequest;
import com.capg.review.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByMentor(Long mentorId);
}