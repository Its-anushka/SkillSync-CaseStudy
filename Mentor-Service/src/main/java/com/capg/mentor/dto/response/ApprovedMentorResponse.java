package com.capg.mentor.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovedMentorResponse {
    private Long mentorId;
    private Long userId;
    private String bio;
    private Integer experience;
    private Double hourlyRate;
}
