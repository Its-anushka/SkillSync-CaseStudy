package com.capg.mentor.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorResponse{
    private Long id;
    private Long userId;
    private String bio;
    private Integer experience;
    private Double rating;
    private Double hourlyRate;
    private List<Long> skillId;
    private List<String> skills;
}
