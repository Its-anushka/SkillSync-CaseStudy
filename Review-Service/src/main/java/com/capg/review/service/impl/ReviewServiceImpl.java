package com.capg.review.service.impl;

import com.capg.review.client.MentorClient;
import com.capg.review.dto.request.ReviewRequest;
import com.capg.review.dto.response.ReviewResponse;
import com.capg.review.entity.Review;
import com.capg.review.exception.ResourceNotFoundException;
import com.capg.review.mapper.ReviewMapper;
import com.capg.review.repository.ReviewRepository;
import com.capg.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MentorClient mentorClient;

    //ADD REVIEW
    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {

        //Validate mentor exists (Feign)
        Object mentor = mentorClient.getMentorById(request.getMentorId());
        if (mentor == null) {
            throw new ResourceNotFoundException("Mentor not found");
        }

        //Save review
        Review review = ReviewMapper.toEntity(request);
        Review savedReview = reviewRepository.save(review);

        //Get all reviews for mentor
        List<Review> reviews = reviewRepository.findByMentorId(request.getMentorId());

        //Calculate average rating
        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        //Update mentor rating (Feign)
        mentorClient.updateMentorRating(request.getMentorId(), avgRating);

        //Return response
        return ReviewMapper.toResponse(savedReview);
    }

    //GET REVIEWS BY MENTOR
    @Override
    public List<ReviewResponse> getReviewsByMentor(Long mentorId) {
        Object mentor = mentorClient.getMentorById(mentorId);
        if (mentor == null) {
            throw new ResourceNotFoundException("Mentor not found");
        }
        List<Review> reviews = reviewRepository.findByMentorId(mentorId);

        return reviews.stream()
                .map(ReviewMapper::toResponse)
                .collect(Collectors.toList());
    }
}