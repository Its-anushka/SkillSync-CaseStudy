package com.capg.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;

/**
 * Standard API Response envelope representing a uniformly 
 * structured response body for the Review Service.
 *
 * @param <T> the type of data enclosed securely within the response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;

    @NonNull
    private String message;

    @Nullable
    private T data;
}