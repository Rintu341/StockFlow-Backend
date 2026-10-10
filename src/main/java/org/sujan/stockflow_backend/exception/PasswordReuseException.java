package org.sujan.stockflow_backend.exception;

public class PasswordReuseException extends RuntimeException {
    public PasswordReuseException(String message) { super(message); }
}