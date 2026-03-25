package com.capg.review.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;

    private Long mentorId;

    private Long userId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}