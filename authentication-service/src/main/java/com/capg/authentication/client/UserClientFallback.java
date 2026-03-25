package com.capg.authentication.client;

import com.capg.authentication.dto.UserProfileRequest;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public void createProfile(String token, UserProfileRequest request) {
        System.out.println("User-service is DOWN. Profile not created.");
    }
}