package org.sujan.stockflow_backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.sujan.stockflow_backend.dto.*;
import org.sujan.stockflow_backend.entity.User;
import org.sujan.stockflow_backend.service.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register-tenant")
    public ResponseEntity<TenantRegisteredResponse> registerTenant(@Valid @RequestBody CreateTenantRequest request) {
        User admin = userService.registerTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TenantRegisteredResponse(admin.getUsername(), admin.getTenantId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<NewUserResponse> register(@Valid @RequestBody RegisterRequest request) {
        NewUserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
//        String token = ;
        return ResponseEntity.ok(userService.login(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }
}