package com.forgefit.controller;

import com.forgefit.dto.AuthResponse;
import com.forgefit.dto.LoginRequest;
import com.forgefit.dto.RegisterRequest;
import com.forgefit.model.User;
import com.forgefit.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * REST Controller for user authentication endpoints.
 * 
 * Handles user registration and login operations with comprehensive input validation,
 * password hashing, and error handling.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    private static final Pattern SPECIAL_CHAR_PATTERN = 
        Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String fullName = request.getFullName() != null ? request.getFullName().trim() : "";
            String email = request.getEmail() != null ? request.getEmail().trim() : "";
            String password = request.getPassword() != null ? request.getPassword().trim() : "";

            if (fullName.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Full name is required"));
            }

            if (fullName.length() > 100) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Full name must not exceed 100 characters"));
            }

            if (email.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Email is required"));
            }

            if (email.length() > 254) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Email must not exceed 254 characters"));
            }

            if (!isValidPasswordStrength(password)) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(
                        "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character"
                    ));
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Email already exists"));
            }

            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setRole("USER");

            User savedUser = userRepository.save(newUser);

            System.out.println("User registered successfully: " + email);

            return ResponseEntity.ok(AuthResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .message("Registration successful")
                .build());

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            System.err.println("Database integrity error during registration: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Email already exists"));
        } catch (Exception e) {
            System.err.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse("An error occurred. Please try again later"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String email = request.getEmail() != null ? request.getEmail().trim() : "";
            String password = request.getPassword() != null ? request.getPassword() : "";

            if (email.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Invalid credentials"));
            }

            if (password.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Invalid credentials"));
            }

            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );

                Optional<User> user = userRepository.findByEmail(email);
                if (user.isPresent()) {
                    System.out.println("User logged in successfully: " + email);
                    return ResponseEntity.ok(AuthResponse.builder()
                        .message("Login successful")
                        .email(user.get().getEmail())
                        .role(user.get().getRole())
                        .build());
                }
            } catch (AuthenticationException e) {
                System.out.println("Login failed for email: " + email + " (invalid credentials)");
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Invalid credentials"));
            }

            return ResponseEntity.badRequest()
                .body(new AuthResponse("Invalid credentials"));

        } catch (Exception e) {
            System.err.println("Error during user login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse("An error occurred. Please try again later"));
        }
    }

    private boolean isValidPasswordStrength(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            return false;
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return false;
        }

        return true;
    }
}
