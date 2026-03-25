package com.capg.mentor.client;

import com.capg.mentor.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class
)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable Long id);

    @PutMapping("/users/admin/role")
    ResponseEntity<Void> updateUserRole(@RequestParam Long id, @RequestParam String role);
}