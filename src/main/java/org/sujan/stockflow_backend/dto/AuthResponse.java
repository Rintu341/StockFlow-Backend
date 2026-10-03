package org.sujan.stockflow_backend.dto;

public class AuthResponse {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String tenantId;
    private String tenantName;
    private Boolean mustChangePassword;
    private long  expiresIn;
    private String tokenType;
    private String token;



    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(String userId, String username, String email, String role, String tenantId, String tenantName, Boolean mustChangePassword, Long expiresIn, String tokenType, String token) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.mustChangePassword = mustChangePassword;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}