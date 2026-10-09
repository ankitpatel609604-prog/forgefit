package com.forgefit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * 
 * Contains the user data required to create a new account.
 * All fields are validated on the backend before processing.
 * 
 * Validation:
 * - fullName: required, max 100 characters
 * - email: required, valid RFC 5322 email format, max 254 characters
 * - password: required, will be validated for strength (8+ chars, mixed case, number, special char)
 */
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 1, max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 128, message = "Password must not exceed 128 characters")
    private String password;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public RegisterRequest() {
    }

    public RegisterRequest(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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
