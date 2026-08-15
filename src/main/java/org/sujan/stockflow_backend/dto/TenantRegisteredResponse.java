package org.sujan.stockflow_backend.dto;

public class TenantRegisteredResponse {
    private String username;
    private Long tenantId;

    public TenantRegisteredResponse(String username, Long tenantId) {
        this.username = username;
        this.tenantId = tenantId;
    }

    public String getUsername() { return username; }
    public Long getTenantId() { return tenantId; }
}