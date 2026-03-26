package com.capg.review.messaging;

import com.capg.review.dto.response.ReviewMessage;
import com.capg.review.entity.Review;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewMessageProducer {
    private RabbitTemplate rabbitTemplate;

    public ReviewMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Review review){
        ReviewMessage reviewMessage = ReviewMessage.builder()
                .id(review.getId())
                .mentorId(review.getMentorId())
                .rating(review.getRating())
                .review(review.getComment())
                .build();
        rabbitTemplate.convertAndSend(reviewMessage);
    }
}
