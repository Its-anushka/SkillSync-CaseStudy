package com.capg.review.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewMessage {
    private Long id;
    private Long mentorId;
    private Integer rating;
    private String review;
}
