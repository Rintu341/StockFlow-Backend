package org.sujan.stockflow_backend.security;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class SecurityErrorWriter {
    private SecurityErrorWriter() {}

    // Only pass constant strings here, so no JSON escaping is needed
    public static void write(HttpServletResponse response, int status,
                             String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"code\":\"" + code
                + "\",\"message\":\"" + message + "\"}");
    }
}