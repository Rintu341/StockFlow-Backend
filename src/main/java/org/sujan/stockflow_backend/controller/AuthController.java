package org.sujan.stockflow_backend.controller;

import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sujan.stockflow_backend.dto.AuthResponse;
import org.sujan.stockflow_backend.dto.CreateTenantRequest;
import org.sujan.stockflow_backend.dto.LoginRequest;
import org.sujan.stockflow_backend.dto.RegisterRequest;
import org.sujan.stockflow_backend.entity.User;
import org.sujan.stockflow_backend.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register-tenant")
    public ResponseEntity<String> registerTenant(@Valid @RequestBody CreateTenantRequest request){
        User admin = userService.registerTenant(request);
        Map<String,String> userDetails = new HashMap<>();
        userDetails.put("userName",admin.getUsername());
        userDetails.put("tenantId",admin.getTenantId().toString());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Gson().toJson(userDetails));
    }

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