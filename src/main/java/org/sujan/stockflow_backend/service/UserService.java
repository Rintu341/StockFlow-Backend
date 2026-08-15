package org.sujan.stockflow_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.sujan.stockflow_backend.dto.*;
import org.sujan.stockflow_backend.entity.Role;
import org.sujan.stockflow_backend.entity.Tenant;
import org.sujan.stockflow_backend.entity.User;
import org.sujan.stockflow_backend.exception.InvalidCredentialsException;
import org.sujan.stockflow_backend.exception.UserAlreadyExistsException;
import org.sujan.stockflow_backend.repository.TenantRepository;
import org.sujan.stockflow_backend.repository.UserRepository;
import org.sujan.stockflow_backend.security.UserPrincipal;

import java.security.SecureRandom;

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

    private static final String PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder();
        // Guarantee at least one of each required character typel
        sb.append("A").append("a").append("1").append("!");
        for (int i = 0; i < 8; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    private Long getCurrentTenantId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getTenantId();
    }

    // Creates a brand-new business + its first Admin user (unchanged — still self-serve)
    public User registerTenant(CreateTenantRequest request) {
        if (tenantRepository.existsByName(request.getTenantName())) {
            throw new UserAlreadyExistsException("Business name already taken");
        }
        /*if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }*/
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        Tenant tenant = tenantRepository.save(new Tenant(request.getTenantName()));

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User admin = new User(request.getUsername(), request.getEmail(), hashedPassword, Role.ADMIN, tenant.getId());
        admin.setMustChangePassword(false); // the founding Admin picks their own password
        return userRepository.save(admin);
    }

    // Admin invites a Manager/Staff into THEIR OWN tenant — system-generated password
    public NewUserResponse register(RegisterRequest request) {
        /*if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }*/
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        Long tenantId = getCurrentTenantId(); // from the logged-in Admin's token, never the request body

        String tempPassword = generateTempPassword();
        String hashedPassword = passwordEncoder.encode(tempPassword);

        User user = new User(request.getUsername(), request.getEmail(), hashedPassword, request.getRole(), tenantId);
        user.setMustChangePassword(true);
        userRepository.save(user);

        return new NewUserResponse(user.getUsername(), user.getEmail(), tempPassword);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public void changePassword(ChangePasswordRequest request) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = principal.getUser();

        boolean currentMatches = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());
        if (!currentMatches) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}