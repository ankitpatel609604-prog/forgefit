package com.forgefit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login requests.
 * 
 * Contains the credentials required for user authentication.
 * Both email and password are required.
 * 
 * Validation:
 * - email: required, valid email format
 * - password: required, non-empty
 */
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
