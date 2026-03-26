package com.capg.session.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.capg.session.dto.ApiResponse;
import com.capg.session.dto.MentorResponse;

@FeignClient(name = "mentor-service", configuration = FeignConfig.class)
public interface MentorServiceClient {
    
    @GetMapping("/mentors/public/{id}")
    ApiResponse<MentorResponse> getMentorById(@PathVariable("id") Long id);
}
