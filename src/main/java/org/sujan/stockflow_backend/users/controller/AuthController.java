package org.sujan.stockflow_backend.users.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sujan.stockflow_backend.users.dto.AuthResponse;
import org.sujan.stockflow_backend.users.dto.LoginRequest;
import org.sujan.stockflow_backend.users.dto.RegisterRequest;
import org.sujan.stockflow_backend.users.entity.User;
import org.sujan.stockflow_backend.users.services.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully with username: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}