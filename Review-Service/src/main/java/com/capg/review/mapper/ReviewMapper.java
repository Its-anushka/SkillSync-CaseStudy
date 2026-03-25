package com.capg.review.mapper;

import com.capg.review.dto.request.ReviewRequest;
import com.capg.review.dto.response.ReviewResponse;
import com.capg.review.entity.Review;

import java.time.LocalDateTime;

public class ReviewMapper {

    //Request → Entity
    public static Review toEntity(ReviewRequest request) {
        return Review.builder()
                .mentorId(request.getMentorId())
                .userId(request.getUserId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    //Entity → Response
    public static ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .mentorId(review.getMentorId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}