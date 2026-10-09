package com.forgefit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_role", columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user. Max 100 characters per RFC requirement.
     */
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    /**
     * Email address used as unique login identifier.
     * Max 254 characters per RFC 5321 compliance.
     */
    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    /**
     * BCrypt hashed password. Never stored as plaintext.
     * Max 255 characters to accommodate BCrypt hash format ($2a$|$2b$|$2y$ + 53 chars).
     */
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    /**
     * User role/authority level. Default is "USER" for regular gym members.
     */
    @Column(name = "role", length = 50, nullable = false)
    private String role = "USER";

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}