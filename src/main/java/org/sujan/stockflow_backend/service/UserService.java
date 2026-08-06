package org.sujan.stockflow_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.sujan.stockflow_backend.dto.CreateTenantRequest;
import org.sujan.stockflow_backend.entity.Role;
import org.sujan.stockflow_backend.entity.Tenant;
import org.sujan.stockflow_backend.exception.InvalidCredentialsException;
import org.sujan.stockflow_backend.exception.UserAlreadyExistsException;
import org.sujan.stockflow_backend.dto.LoginRequest;
import org.sujan.stockflow_backend.dto.RegisterRequest;
import org.sujan.stockflow_backend.entity.User;
import org.sujan.stockflow_backend.repository.TenantRepository;
import org.sujan.stockflow_backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public User registerTenant(CreateTenantRequest request) {
        if (tenantRepository.existsByName(request.getTenantName())) {
            throw new UserAlreadyExistsException("Business name already taken");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        Tenant tenant = tenantRepository.save(new Tenant(request.getTenantName()));

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User admin = new User(request.getUsername(), request.getEmail(), hashedPassword, Role.ADMIN, tenant.getId());
        return userRepository.save(admin);
    }


    public User register(RegisterRequest request) {
        if(!tenantRepository.existsById(request.getTenantId())){
            throw new UserAlreadyExistsException("Invalid tenant Id");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), request.getEmail(), hashedPassword,request.getRole(),request.getTenantId());
        return userRepository.save(user);
    }
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return jwtService.generateToken(user.getUsername());
    }
}