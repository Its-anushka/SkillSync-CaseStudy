package com.capg.mentor.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private boolean active;
}