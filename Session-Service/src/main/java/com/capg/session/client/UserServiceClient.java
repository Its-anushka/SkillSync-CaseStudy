package com.capg.session.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.capg.session.dto.UserDto;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
