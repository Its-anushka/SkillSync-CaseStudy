package com.capg.session.controller;

import com.capg.session.jwt.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/auth")
public class AuthController {

    @PostMapping("/token")
    public ResponseEntity<String> generateToken(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String role = request.get("role");

        if (email == null || role == null) {
            return ResponseEntity.badRequest().body("Email and role are required");
        }

        String token = JWTUtil.generateToken(email, role);
        return ResponseEntity.ok(token);
    }
}
