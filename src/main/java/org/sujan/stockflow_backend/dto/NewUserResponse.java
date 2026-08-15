package org.sujan.stockflow_backend.dto;

public class NewUserResponse {
    private String username;
    private String email;
    private String temporaryPassword;

    public NewUserResponse(String username, String email, String temporaryPassword) {
        this.username = username;
        this.email = email;
        this.temporaryPassword = temporaryPassword;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getTemporaryPassword() { return temporaryPassword; }
}