package com.capg.group.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
@FeignClient(
	    name = "user-service",
	    url = "http://localhost:7072",
	    fallback = UserClientFallback.class
	)
public interface UserClient {

    @GetMapping("/users/me")
    Object getMyProfile(@RequestHeader("Authorization") String token);
}