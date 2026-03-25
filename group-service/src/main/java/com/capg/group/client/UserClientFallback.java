package com.capg.group.client;

import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public Object getMyProfile(String token) {

        System.out.println("⚠️ User Service DOWN → fallback triggered");

        // You decide behavior here
        throw new RuntimeException("User service is unavailable");
    }
}