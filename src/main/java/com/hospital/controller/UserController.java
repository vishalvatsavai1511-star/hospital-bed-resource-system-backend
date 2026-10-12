package com.hospital.controller;

import com.hospital.model.Enums.Role;
import com.hospital.model.User;
import com.hospital.repository.UserRepository;
import com.hospital.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Admin only (enforced in AuthInterceptor). */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository users;
    private final AuthService auth;

    public record UserRequest(String username, String password, Role role) {}

    @GetMapping
    public List<User> all() { return users.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody UserRequest r) {
        if (r.username() == null || r.username().isBlank() || r.password() == null || r.password().length() < 6 || r.role() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, role and a password of 6+ characters are required");
        if (users.findByUsername(r.username().trim()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        User u = new User();
        u.setUsername(r.username().trim());
        u.setPasswordHash(auth.hash(r.password()));
        u.setRole(r.role());
        return users.save(u);
    }
}
