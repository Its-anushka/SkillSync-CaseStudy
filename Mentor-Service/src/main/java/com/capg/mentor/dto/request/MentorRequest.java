package com.capg.mentor.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String bio;

    @Min(0)
    private int experience;

    @Positive
    private double hourlyRate;

    @NotEmpty
    private List<Long> skillIds;

    @NotBlank
    private String email;
}
