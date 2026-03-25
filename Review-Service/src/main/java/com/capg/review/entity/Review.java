package com.capg.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long mentorId;

    private Long userId;

    private Integer rating; // 1–5

    @Column(name = "review_comment")
    private String comment;

    private LocalDateTime createdAt;
}