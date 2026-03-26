package com.capg.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mentor-service")
public interface MentorClient {

    //Validate mentor exists
    @GetMapping("/mentors/public/{id}")
    Object getMentorById(@PathVariable Long id);

    //Update mentor rating
    @PutMapping("/mentors/{id}/rating")
    void updateMentorRating(@PathVariable Long id,
                            @RequestParam Double rating);
}