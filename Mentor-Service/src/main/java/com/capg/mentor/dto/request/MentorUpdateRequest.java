package com.capg.mentor.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorUpdateRequest {

    private String bio;
    @Min(0)
    private int experience;
    @Positive
    private double hourlyRate;
}
