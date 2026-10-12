package com.hospital.controller;

import com.hospital.model.Enums.Role;
import com.hospital.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    public record RegisterRequest(String username, String password, Role role) {}

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        return auth.login(body.get("username"), body.get("password"));
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest body) {
        return auth.register(body.username(), body.password(), body.role());
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if (header != null && header.startsWith("Bearer ")) auth.logout(header.substring(7));
        return Map.of("message", "Logged out");
    }
}
