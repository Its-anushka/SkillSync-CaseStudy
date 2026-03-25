package com.capg.authentication.client;

import com.capg.authentication.dto.UserProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "http://localhost:7072", fallback = UserClientFallback.class)
public interface UserClient {

    @PostMapping("/users/profile")
    void createProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfileRequest request
    );
}