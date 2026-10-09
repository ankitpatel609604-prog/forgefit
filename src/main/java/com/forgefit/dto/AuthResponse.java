package com.forgefit.dto;

/**
 * Data Transfer Object for authentication responses.
 * 
 * Contains the response data returned to the client after login or registration.
 * Notably EXCLUDES the password field for security reasons.
 * 
 * Fields:
 * - id: user's unique identifier (auto-generated)
 * - fullName: user's full name
 * - email: user's email address (login identifier)
 * - role: user's authority level (USER, ADMIN, etc.)
 * - message: status message describing the operation result
 * 
 * Password is intentionally never included in responses.
 */
public class AuthResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String message;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public AuthResponse() {
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public AuthResponse(Long id, String fullName, String email, String role, String message) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    // ============================================================
    // BUILDER PATTERN (for fluent construction)
    // ============================================================

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private Long id;
        private String fullName;
        private String email;
        private String role;
        private String message;

        public AuthResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(id, fullName, email, role, message);
        }
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================

    @Override
    public String toString() {
        return "AuthResponse{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
