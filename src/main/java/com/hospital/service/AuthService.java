package com.hospital.service;

import com.hospital.model.Enums.Role;
import com.hospital.model.User;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public String hash(String raw) { return encoder.encode(raw); }

    public Map<String, Object> register(String username, String password, Role role) {
        if (username == null || username.trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (password == null || password.trim().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
        String clean = username.trim();
        if (users.findByUsername(clean).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User u = new User();
        u.setUsername(clean);
        u.setPasswordHash(hash(password));
        u.setRole(role != null ? role : Role.RECEPTIONIST);
        users.save(u);
        return login(clean, password);
    }

    public Map<String, Object> login(String username, String password) {
        User u = users.findByUsername(username == null ? "" : username.trim())
                .filter(x -> password != null && encoder.matches(password, x.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        String token = UUID.randomUUID().toString();
        sessions.put(token, u);
        return Map.of("token", token, "username", u.getUsername(), "role", u.getRole().name());
    }

    public User find(String token) { return token == null ? null : sessions.get(token); }

    public void logout(String token) { if (token != null) sessions.remove(token); }
}
