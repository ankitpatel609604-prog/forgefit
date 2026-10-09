# Design Document: User Authentication Login Feature

## Overview

The User Authentication Login feature provides a secure, comprehensive system for new gym members to register for accounts and existing members to log in to the ForgeFit platform. The system spans frontend HTML/CSS/JavaScript forms, backend Spring Boot REST API endpoints, Spring Security authentication mechanisms, and a relational database.

**Key Goals:**
- Enable seamless user registration with email-based accounts
- Provide secure login with BCrypt password hashing
- Protect against common web attacks (CSRF, injection, brute force)
- Maintain session state and authorization across requests
- Deliver clear, user-friendly error messages without information leakage
- Support responsive design for desktop and mobile devices

**Technical Stack:**
- Frontend: HTML5, CSS3, vanilla JavaScript (no framework dependencies)
- Backend: Spring Boot 3.3.2, Spring Security, Spring Data JPA
- Database: H2 (development), compatible with MySQL/PostgreSQL
- Password Encoding: BCrypt (cost factor 10+)
- Session Management: Spring Session / HTTP Session Cookies
- CSRF Protection: Spring Security CSRF Token

---

## Architecture

### High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                       Browser / Client                           │
├─────────────────────────────────────────────────────────────────┤
│                         login.html                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Login Form | Signup Form                                │  │
│  │  - Email Input                - Full Name Input           │  │
│  │  - Password Input              - Email Input              │  │
│  │  - Remember Me Checkbox        - Password Input           │  │
│  │  - Login Button                - Confirm Password         │  │
│  │                                - Terms Checkbox           │  │
│  │                                - Sign Up Button           │  │
│  └──────────────────────────────────────────────────────────┘  │
│              ↓ (JavaScript Validation & HTTP Requests)         │
└─────────────────────────────────────────────────────────────────┘
                              ↓ HTTPS
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Backend                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────┐  │
│  │            AuthController                                │  │
│  │  POST /api/auth/register → register()                   │  │
│  │  POST /api/auth/login     → login()                     │  │
│  └────────────┬───────────────────────────────────────────┘  │
│               ↓                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │     Spring Security (SecurityConfig)                     │  │
│  │  - PasswordEncoder (BCrypt)                              │  │
│  │  - AuthenticationManager                                 │  │
│  │  - DaoAuthenticationProvider                             │  │
│  │  - CustomUserDetailsService                              │  │
│  │  - SecurityFilterChain (CSRF, Authorization)             │  │
│  └────────────┬───────────────────────────────────────────┘  │
│               ↓                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │      UserRepository (Spring Data JPA)                    │  │
│  │  - findByEmail(String email)                             │  │
│  │  - save(User user)                                       │  │
│  │  - Custom queries as needed                              │  │
│  └────────────┬───────────────────────────────────────────┘  │
│               ↓                                                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓ SQL
┌─────────────────────────────────────────────────────────────────┐
│                    H2 Database (Development)                     │
├─────────────────────────────────────────────────────────────────┤
│  users table:                                                    │
│  ├── id (PK, auto-increment)                                   │
│  ├── full_name (varchar 100)                                   │
│  ├── email (varchar 254, UNIQUE, NOT NULL)                    │
│  ├── password (varchar 255, NOT NULL - BCrypt hash)           │
│  ├── role (varchar 50, default 'USER')                        │
│  └── [future] status, membership_tier, created_at             │
└─────────────────────────────────────────────────────────────────┘
```

### Component Interactions - Registration Flow

```
User (Browser)
    │
    ├─→ Fill signup form
    │
    ├─→ JavaScript validates:
    │   - Email format (RFC 5322)
    │   - Password strength (8+ chars, mixed case, number, special)
    │   - Password confirmation match
    │   - Max lengths (email 254, password 128, name 100)
    │   - Whitespace trimmed
    │
    ├─→ Display validation errors (if any)
    │
    ├─→ POST /api/auth/register
    │   {
    │     "fullName": "John Doe",
    │     "email": "john@example.com",
    │     "password": "SecurePass123!"
    │   }
    │
    ├─→ AuthController.register()
    │   │
    │   ├─→ Check if email exists (UserRepository.findByEmail)
    │   │
    │   ├─→ If exists: return 400 {"message": "Email already exists"}
    │   │
    │   ├─→ If new:
    │   │   ├─→ Hash password with BCrypt
    │   │   ├─→ Set role = "USER"
    │   │   ├─→ Save User to database
    │   │
    │   └─→ Return 200 with response:
    │       {
    │         "id": 1,
    │         "fullName": "John Doe",
    │         "email": "john@example.com",
    │         "role": "USER"
    │       }
    │
    └─→ Display success message + redirect to login or dashboard
```

### Component Interactions - Login Flow

```
User (Browser)
    │
    ├─→ Fill login form
    │
    ├─→ JavaScript validates:
    │   - Email is not empty
    │   - Password is not empty
    │   - Optional: email format
    │
    ├─→ Display validation errors (if any)
    │
    ├─→ Disable Login button + show loading indicator
    │
    ├─→ POST /api/auth/login (with CSRF token if enabled)
    │   {
    │     "email": "john@example.com",
    │     "password": "SecurePass123!"
    │   }
    │
    ├─→ AuthController.login()
    │   │
    │   ├─→ Extract email and password from request
    │   │
    │   ├─→ AuthenticationManager.authenticate(
    │   │      new UsernamePasswordAuthenticationToken(email, password)
    │   │   )
    │   │
    │   ├─→ If authentication succeeds:
    │   │   ├─→ Create Spring Security Authentication
    │   │   ├─→ Set SecurityContextHolder
    │   │   ├─→ Return 200 with response:
    │   │       {
    │   │         "message": "Login successful",
    │   │         "email": "john@example.com",
    │   │         "role": "USER"
    │   │       }
    │   │   └─→ Spring Session creates HttpSession cookie
    │   │
    │   └─→ If authentication fails:
    │       ├─→ UserDetailsService.loadUserByUsername() throws UsernameNotFoundException
    │       ├─→ Or PasswordEncoder.matches() returns false
    │       └─→ Return 400 with response:
    │           {
    │             "message": "Invalid credentials"
    │           }
    │
    ├─→ Re-enable Login button
    │
    ├─→ If successful: store email in localStorage (optional), redirect to dashboard
    │
    └─→ If failed: display error message on login page
```

---

## Components and Interfaces

### 1. Frontend Components

#### 1.1 login.html Structure

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ForgeFit - Login & Registration</title>
    <link rel="stylesheet" href="../styles.css">
    <style>
        /* login-specific styles */
        .auth-container { /* two-column or tabbed layout */ }
        .login-section { /* login form container */ }
        .signup-section { /* signup form container */ }
        .form-error { /* error message styling */ }
        .form-success { /* success message styling */ }
    </style>
</head>
<body>
    <!-- Navigation back to index -->
    <nav>
        <a href="index.html">← Back to ForgeFit</a>
    </nav>

    <!-- Main authentication container -->
    <div class="auth-container">
        <!-- Login Tab/Section -->
        <div class="login-section" id="loginSection">
            <h2>Login to Your Account</h2>
            <form id="loginForm" novalidate>
                <!-- CSRF Token (if enabled) -->
                <input type="hidden" id="csrfToken" name="_csrf" />

                <!-- Email Field -->
                <div class="form-group">
                    <label for="loginEmail">Email Address</label>
                    <input 
                        type="email" 
                        id="loginEmail" 
                        name="email" 
                        placeholder="Enter your email"
                        required 
                        aria-required="true"
                        autocomplete="email"
                    />
                    <span class="form-error" id="loginEmailError"></span>
                </div>

                <!-- Password Field -->
                <div class="form-group">
                    <label for="loginPassword">Password</label>
                    <input 
                        type="password" 
                        id="loginPassword" 
                        name="password" 
                        placeholder="Enter your password"
                        required 
                        aria-required="true"
                        autocomplete="current-password"
                    />
                    <span class="form-error" id="loginPasswordError"></span>
                </div>

                <!-- Remember Me Checkbox (optional) -->
                <div class="form-group checkbox">
                    <input 
                        type="checkbox" 
                        id="rememberMe" 
                        name="rememberMe"
                    />
                    <label for="rememberMe">Remember me</label>
                </div>

                <!-- Form-level error -->
                <div class="form-error" id="loginFormError"></div>

                <!-- Form-level success -->
                <div class="form-success" id="loginFormSuccess"></div>

                <!-- Submit Button -->
                <button 
                    type="submit" 
                    id="loginBtn" 
                    class="btn btn-primary"
                    aria-label="Log in to your account"
                >
                    Login
                </button>
            </form>

            <!-- Loading Indicator -->
            <div id="loginLoading" class="loading-indicator" style="display: none;">
                <span class="spinner"></span> Logging in...
            </div>

            <!-- Switch to Signup -->
            <p>Don't have an account? 
                <a href="#" id="switchToSignup" class="tab-switch">Sign up here</a>
            </p>
        </div>

        <!-- Signup Tab/Section -->
        <div class="signup-section" id="signupSection" style="display: none;">
            <h2>Create Your Account</h2>
            <form id="signupForm" novalidate>
                <!-- CSRF Token (if enabled) -->
                <input type="hidden" id="csrfToken" name="_csrf" />

                <!-- Full Name Field -->
                <div class="form-group">
                    <label for="signupFullName">Full Name</label>
                    <input 
                        type="text" 
                        id="signupFullName" 
                        name="fullName" 
                        placeholder="Enter your full name"
                        required 
                        aria-required="true"
                        autocomplete="name"
                        maxlength="100"
                    />
                    <span class="form-error" id="signupFullNameError"></span>
                </div>

                <!-- Email Field -->
                <div class="form-group">
                    <label for="signupEmail">Email Address</label>
                    <input 
                        type="email" 
                        id="signupEmail" 
                        name="email" 
                        placeholder="Enter your email"
                        required 
                        aria-required="true"
                        autocomplete="email"
                        maxlength="254"
                    />
                    <span class="form-error" id="signupEmailError"></span>
                </div>

                <!-- Password Field -->
                <div class="form-group">
                    <label for="signupPassword">Password</label>
                    <input 
                        type="password" 
                        id="signupPassword" 
                        name="password" 
                        placeholder="Create a strong password"
                        required 
                        aria-required="true"
                        autocomplete="new-password"
                        maxlength="128"
                    />
                    <span class="form-error" id="signupPasswordError"></span>
                    <small class="password-requirements">
                        Password must contain: 8+ characters, uppercase, lowercase, number, special character
                    </small>
                </div>

                <!-- Password Confirmation Field -->
                <div class="form-group">
                    <label for="signupPasswordConfirm">Confirm Password</label>
                    <input 
                        type="password" 
                        id="signupPasswordConfirm" 
                        name="passwordConfirm" 
                        placeholder="Confirm your password"
                        required 
                        aria-required="true"
                        autocomplete="new-password"
                        maxlength="128"
                    />
                    <span class="form-error" id="signupPasswordConfirmError"></span>
                </div>

                <!-- Terms and Conditions Checkbox -->
                <div class="form-group checkbox">
                    <input 
                        type="checkbox" 
                        id="termsAccept" 
                        name="termsAccept"
                        required
                        aria-required="true"
                    />
                    <label for="termsAccept">
                        I accept the 
                        <a href="/terms" target="_blank">Terms and Conditions</a>
                    </label>
                    <span class="form-error" id="termsAcceptError"></span>
                </div>

                <!-- Form-level error -->
                <div class="form-error" id="signupFormError"></div>

                <!-- Form-level success -->
                <div class="form-success" id="signupFormSuccess"></div>

                <!-- Submit Button -->
                <button 
                    type="submit" 
                    id="signupBtn" 
                    class="btn btn-primary"
                    aria-label="Create your account"
                >
                    Sign Up
                </button>
            </form>

            <!-- Loading Indicator -->
            <div id="signupLoading" class="loading-indicator" style="display: none;">
                <span class="spinner"></span> Creating account...
            </div>

            <!-- Switch to Login -->
            <p>Already have an account? 
                <a href="#" id="switchToLogin" class="tab-switch">Log in here</a>
            </p>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="auth.js"></script>
</body>
</html>
```

#### 1.2 Frontend JavaScript Validation (auth.js)

```javascript
// auth.js - Frontend validation and API integration

class AuthForm {
    constructor() {
        this.loginForm = document.getElementById('loginForm');
        this.signupForm = document.getElementById('signupForm');
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Form submissions
        this.loginForm.addEventListener('submit', (e) => this.handleLoginSubmit(e));
        this.signupForm.addEventListener('submit', (e) => this.handleSignupSubmit(e));

        // Tab switching
        document.getElementById('switchToSignup').addEventListener('click', (e) => {
            e.preventDefault();
            this.showSignupForm();
        });
        document.getElementById('switchToLogin').addEventListener('click', (e) => {
            e.preventDefault();
            this.showLoginForm();
        });

        // Real-time validation on blur
        this.setupBlurValidation();
    }

    setupBlurValidation() {
        // Login form
        document.getElementById('loginEmail').addEventListener('blur', (e) => {
            this.validateLoginEmail(e.target.value);
        });

        // Signup form
        document.getElementById('signupFullName').addEventListener('blur', (e) => {
            this.validateFullName(e.target.value);
        });
        document.getElementById('signupEmail').addEventListener('blur', (e) => {
            this.validateSignupEmail(e.target.value);
        });
        document.getElementById('signupPassword').addEventListener('blur', (e) => {
            this.validatePassword(e.target.value);
        });
        document.getElementById('signupPasswordConfirm').addEventListener('blur', (e) => {
            this.validatePasswordMatch();
        });
    }

    // ============================================================
    // VALIDATION FUNCTIONS
    // ============================================================

    /**
     * Email Validation
     * - RFC 5322 compliant regex pattern
     * - Max 254 characters (RFC 5321)
     */
    isValidEmail(email) {
        const rfc5322Regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return email.length <= 254 && rfc5322Regex.test(email);
    }

    /**
     * Password Strength Validation
     * Requirements: 8+ chars, uppercase, lowercase, number, special char
     */
    isValidPassword(password) {
        if (password.length < 8) return false;
        if (!/[A-Z]/.test(password)) return false;  // Uppercase
        if (!/[a-z]/.test(password)) return false;  // Lowercase
        if (!/\d/.test(password)) return false;      // Number
        if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) return false; // Special char
        return true;
    }

    /**
     * Check missing character types in password
     */
    getPasswordRequirements(password) {
        const missing = [];
        if (password.length < 8) missing.push('8+ characters');
        if (!/[A-Z]/.test(password)) missing.push('uppercase letter');
        if (!/[a-z]/.test(password)) missing.push('lowercase letter');
        if (!/\d/.test(password)) missing.push('number');
        if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) missing.push('special character');
        return missing;
    }

    /**
     * Full name validation
     * - Not empty (after trimming)
     * - Max 100 characters
     */
    isValidFullName(name) {
        return name.trim().length > 0 && name.length <= 100;
    }

    // ============================================================
    // LOGIN FORM VALIDATION
    // ============================================================

    validateLoginEmail(email) {
        const errorEl = document.getElementById('loginEmailError');
        email = email.trim();

        if (!email) {
            errorEl.textContent = 'Email is required';
            return false;
        }
        if (!this.isValidEmail(email)) {
            errorEl.textContent = 'Please enter a valid email address';
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    validateLoginPassword(password) {
        const errorEl = document.getElementById('loginPasswordError');
        
        if (!password) {
            errorEl.textContent = 'Password is required';
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    // ============================================================
    // SIGNUP FORM VALIDATION
    // ============================================================

    validateFullName(name) {
        const errorEl = document.getElementById('signupFullNameError');
        name = name.trim();

        if (!name) {
            errorEl.textContent = 'Full name is required';
            return false;
        }
        if (name.length > 100) {
            errorEl.textContent = 'Full name must not exceed 100 characters';
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    validateSignupEmail(email) {
        const errorEl = document.getElementById('signupEmailError');
        email = email.trim();

        if (!email) {
            errorEl.textContent = 'Email is required';
            return false;
        }
        if (email.length > 254) {
            errorEl.textContent = 'Email must not exceed 254 characters';
            return false;
        }
        if (!this.isValidEmail(email)) {
            errorEl.textContent = 'Please enter a valid email address';
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    validatePassword(password) {
        const errorEl = document.getElementById('signupPasswordError');
        
        if (!password) {
            errorEl.textContent = 'Password is required';
            return false;
        }
        if (password.length > 128) {
            errorEl.textContent = 'Password must not exceed 128 characters';
            return false;
        }
        if (!this.isValidPassword(password)) {
            const missing = this.getPasswordRequirements(password);
            errorEl.textContent = `Password must contain: ${missing.join(', ')}`;
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    validatePasswordMatch() {
        const password = document.getElementById('signupPassword').value;
        const confirm = document.getElementById('signupPasswordConfirm').value;
        const errorEl = document.getElementById('signupPasswordConfirmError');

        if (password !== confirm) {
            errorEl.textContent = 'Passwords do not match';
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    validateTermsAccept() {
        const checkbox = document.getElementById('termsAccept');
        const errorEl = document.getElementById('termsAcceptError');

        if (!checkbox.checked) {
            errorEl.textContent = 'You must accept the Terms and Conditions';
            return false;
        }
        errorEl.textContent = '';
        return true;
    }

    // ============================================================
    // FORM SUBMISSION HANDLERS
    // ============================================================

    async handleLoginSubmit(e) {
        e.preventDefault();

        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;

        // Validate
        const emailValid = this.validateLoginEmail(email);
        const passwordValid = this.validateLoginPassword(password);

        if (!emailValid || !passwordValid) {
            return;
        }

        // Disable button and show loading
        const loginBtn = document.getElementById('loginBtn');
        const loading = document.getElementById('loginLoading');
        loginBtn.disabled = true;
        loading.style.display = 'block';

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-Token': document.getElementById('csrfToken').value || ''
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok) {
                // Success
                document.getElementById('loginFormSuccess').textContent = data.message || 'Login successful!';
                localStorage.setItem('userEmail', email);
                // Redirect to dashboard after 1 second
                setTimeout(() => {
                    window.location.href = '/dashboard';
                }, 1000);
            } else {
                // Failure
                document.getElementById('loginFormError').textContent = data.message || 'Login failed. Please try again.';
            }
        } catch (error) {
            console.error('Login error:', error);
            document.getElementById('loginFormError').textContent = 'An error occurred. Please try again later.';
        } finally {
            loginBtn.disabled = false;
            loading.style.display = 'none';
        }
    }

    async handleSignupSubmit(e) {
        e.preventDefault();

        const fullName = document.getElementById('signupFullName').value.trim();
        const email = document.getElementById('signupEmail').value.trim();
        const password = document.getElementById('signupPassword').value;
        const passwordConfirm = document.getElementById('signupPasswordConfirm').value;

        // Validate all fields
        const nameValid = this.validateFullName(fullName);
        const emailValid = this.validateSignupEmail(email);
        const passwordValid = this.validatePassword(password);
        const matchValid = this.validatePasswordMatch();
        const termsValid = this.validateTermsAccept();

        if (!nameValid || !emailValid || !passwordValid || !matchValid || !termsValid) {
            return;
        }

        // Disable button and show loading
        const signupBtn = document.getElementById('signupBtn');
        const loading = document.getElementById('signupLoading');
        signupBtn.disabled = true;
        loading.style.display = 'block';

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-Token': document.getElementById('csrfToken').value || ''
                },
                body: JSON.stringify({
                    fullName,
                    email,
                    password
                })
            });

            const data = await response.json();

            if (response.ok) {
                // Success
                document.getElementById('signupFormSuccess').textContent = 'Account created successfully! Redirecting to login...';
                // Redirect to login after 2 seconds
                setTimeout(() => {
                    this.showLoginForm();
                    // Pre-fill email
                    document.getElementById('loginEmail').value = email;
                }, 2000);
            } else {
                // Failure
                document.getElementById('signupFormError').textContent = data.message || 'Registration failed. Please try again.';
            }
        } catch (error) {
            console.error('Signup error:', error);
            document.getElementById('signupFormError').textContent = 'An error occurred. Please try again later.';
        } finally {
            signupBtn.disabled = false;
            loading.style.display = 'none';
        }
    }

    // ============================================================
    // UI STATE MANAGEMENT
    // ============================================================

    showLoginForm() {
        document.getElementById('loginSection').style.display = 'block';
        document.getElementById('signupSection').style.display = 'none';
    }

    showSignupForm() {
        document.getElementById('loginSection').style.display = 'none';
        document.getElementById('signupSection').style.display = 'block';
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new AuthForm();
});
```

### 2. Backend Components

#### 2.1 User Entity (Enhanced)

```java
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

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", length = 50, nullable = false)
    private String role = "USER";

    // Optional future fields
    // @Column(name = "status")
    // private String status = "active";
    //
    // @Column(name = "membership_tier")
    // private String membershipTier;
    //
    // @Column(name = "created_at")
    // @Temporal(TemporalType.TIMESTAMP)
    // private Date createdAt = new Date();
}
```

#### 2.2 UserRepository (New)

```java
package com.forgefit.repository;

import com.forgefit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by email address (case-sensitive)
     * @param email the email address to search for
     * @return Optional containing the User if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
}
```

#### 2.3 AuthController (Existing - Reviewed)

Already implemented with proper error handling and response structure.

#### 2.4 SecurityConfig (Existing - Reviewed)

Already configured with BCrypt password encoding and proper authorization rules.

#### 2.5 CustomUserDetailsService (Existing - Reviewed)

Already implements UserDetailsService to load users by email.

---

## Data Models

### User Entity Schema

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);
```

### Database Design Rationale

1. **id (BIGINT AUTO_INCREMENT PRIMARY KEY)**
   - Auto-incremented unique identifier
   - BIGINT allows for billions of users

2. **full_name (VARCHAR(100))**
   - Stores user's display name
   - Optional field for flexibility
   - Limited to 100 chars per requirements

3. **email (VARCHAR(254) UNIQUE NOT NULL)**
   - RFC 5321 maximum email length (254 chars)
   - UNIQUE constraint prevents duplicate accounts
   - NOT NULL ensures all users have an email
   - UNIQUE INDEX for fast lookups

4. **password (VARCHAR(255) NOT NULL)**
   - Stores BCrypt hash (typically 60 chars, but 255 for future compatibility)
   - NOT NULL required
   - Never stores plaintext

5. **role (VARCHAR(50) NOT NULL DEFAULT 'USER')**
   - Authorization level for user
   - Default value "USER" for new registrations
   - INDEX on role for quick authorization queries

6. **created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)**
   - Audit trail for account creation
   - Auto-set on insert
   - Useful for analytics and security investigations

### API Schemas

#### 2.1 POST /api/auth/register

**Request:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (Success - HTTP 200):**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Response (Failure - HTTP 400):**
```json
{
  "message": "Email already exists"
}
```

or

```json
{
  "message": "Invalid input"
}
```

#### 2.2 POST /api/auth/login

**Request:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (Success - HTTP 200):**
```json
{
  "message": "Login successful",
  "email": "john@example.com",
  "role": "USER"
}
```

**Response (Failure - HTTP 400):**
```json
{
  "message": "Invalid credentials"
}
```

### Error Response Format

All error responses follow this structure:
```json
{
  "message": "Descriptive error message"
}
```

**Key Principles:**
- Messages are user-friendly and don't expose sensitive information
- No stack traces or internal server details
- Consistent message format for consistency

---

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Email Format Validation

**For any** user input string, if it contains the character `@` and valid domain structure, email validation SHALL accept it; otherwise, validation SHALL reject it.

**Validates: Requirements 1.2, 4.2**

### Property 2: Password Strength Enforcement

**For any** string input, if it contains fewer than 8 characters OR lacks uppercase letters OR lacks lowercase letters OR lacks digits OR lacks special characters, password validation SHALL reject it with specific feedback; otherwise, it SHALL accept it.

**Validates: Requirements 1.3, 4.3, 4.4**

### Property 3: Email Uniqueness Constraint

**For any** two distinct user registrations with the same email address, the second registration attempt SHALL fail with message "Email already exists" and SHALL NOT create a new user record; the original user record SHALL remain unchanged.

**Validates: Requirements 1.6**

### Property 4: Default User Role Assignment

**For any** new user registration with no explicit role assignment, the saved user's role field in the database SHALL equal "USER".

**Validates: Requirements 1.7, 5.5**

### Property 5: Password Hash Security - No Plaintext Storage

**For any** user in the database, the stored password value SHALL NOT equal any plaintext password submitted during registration or login, and SHALL match the BCrypt hash format (starting with `$2a$`, `$2b$`, or `$2y$` followed by numeric cost and 53 additional characters).

**Validates: Requirements 3.1, 3.3**

### Property 6: Login Error Message Consistency

**For any** login attempt with an unregistered email OR incorrect password, the response SHALL return HTTP 400 with message "Invalid credentials"; the message SHALL be identical regardless of whether the email doesn't exist or the password is wrong (no distinguishing error).

**Validates: Requirements 2.5, 2.6**

### Property 7: Response Excludes Password Field

**For any** successful registration or login response, the returned JSON object SHALL NOT contain a "password" field; all other user fields (id, fullName, email, role) SHALL be present and accurate.

**Validates: Requirements 5.3, 5.6**

### Property 8: Empty Email Rejection in Login

**For any** login attempt where the email field is empty (zero length string or only whitespace), login validation SHALL reject the submission and display error "Email is required" without making a server request.

**Validates: Requirements 2.2**

### Property 9: Email Max Length Validation

**For any** email input string, if it exceeds 254 characters, validation SHALL reject it with error message "Email must not exceed 254 characters"; if it is 254 characters or fewer, it SHALL pass initial validation (RFC 5321 compliance).

**Validates: Requirements 9.1**

### Property 10: Password Max Length Validation

**For any** password input string, if it exceeds 128 characters, validation SHALL reject it with error message "Password must not exceed 128 characters"; if it is 128 characters or fewer, it SHALL pass length validation.

**Validates: Requirements 9.2**

### Property 11: Full Name Max Length Validation

**For any** full name input string, if it exceeds 100 characters, validation SHALL reject it with error message "Full name must not exceed 100 characters"; if it is 100 characters or fewer, it SHALL pass length validation.

**Validates: Requirements 9.3**

### Property 12: Whitespace Trimming

**For any** form field input with leading or trailing whitespace, after submission, the stored value in the database SHALL have leading and trailing whitespace removed (trimmed); the trimmed value SHALL be used for all validation checks and storage.

**Validates: Requirements 9.4**

### Property 13: Input Sanitization Against Injection

**For any** form field input containing SQL special characters (`, ', ", ;, --, /*, */) OR HTML/XSS special characters (<, >, &, {, }, |, \), the system SHALL sanitize the input before storage or use in database queries to prevent injection attacks; user-provided content SHALL be parameterized in all database queries.

**Validates: Requirements 9.5, 9.6**

### Property 14: Unique User ID Generation

**For any** two distinct user registrations, the generated user ID values SHALL be unique; no two users in the database SHALL share the same ID.

**Validates: Requirements 10.3**

### Property 15: Complete User Retrieval Round-Trip

**For any** user data submitted to POST /api/auth/register, after successful registration, querying the database with UserRepository.findByEmail() SHALL return a User object containing the submitted fullName, email, and the submitted password hashed with BCrypt (not plaintext), and id, and role = "USER".

**Validates: Requirements 10.4**

### Property 16: Successful Login Response Structure

**For any** successful login, the response JSON SHALL contain:
- "message" field with value "Login successful"
- "email" field matching the login request email
- "role" field with value "USER" (or appropriate role)
- No "password" field

**Validates: Requirements 11.1, 11.2, 11.3**

### Property 17: Successful Registration Response Structure

**For any** successful registration, the response JSON SHALL contain:
- "id" field with a positive integer
- "fullName" field matching submitted full name
- "email" field matching submitted email
- "role" field with value "USER"
- No "password" field

**Validates: Requirements 11.4**

### Property 18: Error Response Has Message Field

**For any** failed authentication request (registration or login), the response JSON SHALL contain a "message" field with a human-readable error description; responses SHALL NOT be empty or omit the message field.

**Validates: Requirements 11.5**

---

## Security Mechanisms

### 1. Password Hashing

**Implementation:**
```java
PasswordEncoder encoder = new BCryptPasswordEncoder(10);  // cost factor 10
String hashedPassword = encoder.encode(plainPassword);
```

**Key Properties:**
- BCrypt cost factor: minimum 10 (adaptive, slows brute-force)
- Automatic salt generation (included in hash output)
- Hash format: `$2a$10$[22 char salt][31 char hash]`
- Constant-time comparison in authentication

**Security Guarantees:**
- Plaintext passwords never stored
- Same password generates different hash each time (due to salt)
- Hash resistant to rainbow tables and precomputed attacks

### 2. Session Management

**Mechanism:**
- Spring Session with HTTP Session Cookies
- Session created upon successful login
- SessionId stored in secure, HttpOnly cookie
- Cookie attributes: Secure, HttpOnly, SameSite=Strict

**Flow:**
1. User authenticates successfully
2. Spring Security sets SecurityContext
3. Servlet container creates HttpSession
4. Session cookie sent to browser
5. Browser includes cookie in subsequent requests
6. Server validates session on each request

**Logout:**
```java
SecurityContextHolder.clearContext();
HttpSession session = request.getSession(false);
if (session != null) {
    session.invalidate();
}
```

### 3. CSRF Protection

**Current Configuration:**
```java
.csrf(AbstractHttpConfigurer::disable)  // DISABLED for testing
```

**Production Configuration:**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/auth/**")  // if needed
)
```

**CSRF Token Flow:**
1. GET /login.html → server includes CSRF token in page or meta tag
2. Form submission includes token in `_csrf` header or form field
3. Server validates token before processing POST

**Frontend Integration:**
```javascript
// Extract CSRF token from hidden input
const csrfToken = document.getElementById('csrfToken').value;

// Include in all POST requests
headers: {
    'X-CSRF-Token': csrfToken
}
```

### 4. Authorization

**Setup:**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .anyRequest().authenticated()
)
```

**Enforcement:**
- Public endpoints: `/api/auth/register`, `/api/auth/login`, `/login.html`
- Protected endpoints: All others require valid session
- Invalid/missing session: 401 Unauthorized or redirect to login

### 5. Error Handling and Information Disclosure

**Principle:** Generic error messages prevent account enumeration

**Implementation:**
```java
// Wrong email vs wrong password - same message
if (user.isEmpty()) {
    return ResponseEntity.badRequest().body(
        Map.of("message", "Invalid credentials")
    );
}

Authentication authentication = authenticationManager.authenticate(...);
if (!authentication.isAuthenticated()) {
    return ResponseEntity.badRequest().body(
        Map.of("message", "Invalid credentials")
    );
}
```

**Benefits:**
- Attacker cannot determine if email is registered
- No account enumeration vulnerability
- Brute-force attacks equally ineffective for email discovery

---

## Error Handling

### Frontend Error Handling

```javascript
// Validation errors (before submission)
const errors = {
    emailRequired: 'Email is required',
    emailInvalid: 'Please enter a valid email address',
    emailTooLong: 'Email must not exceed 254 characters',
    passwordRequired: 'Password is required',
    passwordTooShort: 'Password must be at least 8 characters long',
    passwordMissing: 'Password must contain: [missing types]',
    passwordTooLong: 'Password must not exceed 128 characters',
    passwordMismatch: 'Passwords do not match',
    nameRequired: 'Full name is required',
    nameTooLong: 'Full name must not exceed 100 characters',
    termsRequired: 'You must accept the Terms and Conditions'
};

// Display inline errors immediately below each field
displayError(fieldId, message);

// Display form-level errors above buttons
displayFormError(formId, message);
```

### Backend Error Handling

```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    // Validation errors
    if (user.getEmail() == null || user.getEmail().isBlank()) {
        return ResponseEntity.badRequest().body(
            Map.of("message", "Email is required")
        );
    }
    
    // Business logic errors
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        return ResponseEntity.badRequest().body(
            Map.of("message", "Email already exists")
        );
    }
    
    // Processing
    try {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(Map.of(...));
    } catch (Exception e) {
        // Never expose internal errors
        return ResponseEntity.status(500).body(
            Map.of("message", "An error occurred. Please try again later.")
        );
    }
}
```

### HTTP Status Codes

| Status | Scenario | Response Body |
|--------|----------|---|
| 200 | Successful login/registration | User data with message |
| 400 | Validation error, email exists, invalid credentials | `{"message": "..."}` |
| 401 | Unauthenticated access to protected endpoint | Redirect to login |
| 403 | Invalid CSRF token | `{"message": "Access denied"}` |
| 500 | Server error | `{"message": "An error occurred. Please try again later."}` |

---

## Testing Strategy

### Unit Testing

**Focus:** Individual validation functions, utility methods

**Examples:**
- `isValidEmail(String email)` - valid/invalid email formats
- `isValidPassword(String password)` - password strength checks
- `isValidFullName(String name)` - name length constraints

**Framework:** JUnit 5 + Mockito

```java
@Test
void testValidEmailFormat() {
    AuthForm form = new AuthForm();
    assertTrue(form.isValidEmail("user@example.com"));
    assertTrue(form.isValidEmail("user+tag@sub.example.co.uk"));
    assertFalse(form.isValidEmail("invalid"));
    assertFalse(form.isValidEmail("user@"));
}

@Test
void testPasswordStrength() {
    AuthForm form = new AuthForm();
    assertTrue(form.isValidPassword("SecurePass123!"));
    assertFalse(form.isValidPassword("weak"));
    assertFalse(form.isValidPassword("NoNumbers!"));
}
```

### Integration Testing

**Focus:** API endpoints, database interactions, authentication flow

**Examples:**
- Register new user successfully
- Reject duplicate email registration
- Login with valid credentials
- Reject login with wrong password
- Session creation and validation

**Framework:** Spring Boot Test + MockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testSuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"John\",\"email\":\"john@test.com\",\"password\":\"SecurePass123!\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("john@test.com"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }
    
    @Test
    void testDuplicateEmailRejection() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"John\",\"email\":\"john@test.com\",\"password\":\"SecurePass123!\"}"))
            .andExpect(status().isOk());
        
        // Try to register with same email
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Jane\",\"email\":\"john@test.com\",\"password\":\"SecurePass456!\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Email already exists"));
    }
}
```

### Property-Based Testing (with fast-check or similar)

**Focus:** Universal properties across generated inputs

**Configuration:** Minimum 100 iterations per property

**Examples:**

```javascript
// Property test: Email validation (JavaScript)
test('Property 1: Valid emails pass validation', () => {
    fc.assert(
        fc.property(fc.emailAddress(), (email) => {
            expect(isValidEmail(email)).toBe(true);
        }),
        { numRuns: 100 }
    );
});

// Property test: Password strength (JavaScript)
test('Property 2: Weak passwords fail validation', () => {
    fc.assert(
        fc.property(fc.string({maxLength: 7}), (weakPassword) => {
            expect(isValidPassword(weakPassword)).toBe(false);
        }),
        { numRuns: 100 }
    );
});
```

### Acceptance Testing

**Focus:** End-to-end workflows, UI interactions

**Examples:**
- User navigates to login.html
- User fills signup form and submits
- Success message appears and redirect occurs
- User fills login form and submits
- Logged-in user can access dashboard

**Framework:** Selenium, Cypress, or Playwright

### Security Testing

**Focus:** Authentication bypass, injection attacks, authorization violations

**Test Cases:**
- SQL injection in email field: `"; DROP TABLE users; --`
- XSS in name field: `<script>alert('xss')</script>`
- CSRF attack: POST from different origin
- Brute force: Rapid login attempts
- Password plaintext leakage: Verify never in logs or responses
- Session fixation: Verify new session after login
- Authorization bypass: Access protected endpoints without session

---

## Implementation Checklist

### Backend

- [ ] Create UserRepository with findByEmail() method
- [ ] Update User entity with proper column constraints and indexes
- [ ] Implement validation in AuthController for input length/format
- [ ] Add server-side email uniqueness check in register()
- [ ] Implement password hashing in register() with BCrypt
- [ ] Implement login authentication with AuthenticationManager
- [ ] Return consistent "Invalid credentials" for both email/password failures
- [ ] Exclude password from response JSONs
- [ ] Add default role "USER" for new registrations
- [ ] Implement error handling with user-friendly messages
- [ ] Test all endpoints with happy path and error cases
- [ ] Add database indexes on email and role columns

### Frontend

- [ ] Create login.html with login and signup sections (tabbed or two-column)
- [ ] Implement auth.js with validation functions
- [ ] Validate email format (RFC 5322 pattern)
- [ ] Validate password strength (8+ chars, mixed case, number, special)
- [ ] Validate password confirmation matching
- [ ] Validate full name (max 100 chars)
- [ ] Validate email max length (254 chars)
- [ ] Validate password max length (128 chars)
- [ ] Implement whitespace trimming
- [ ] Implement on-blur field validation
- [ ] Display inline error messages
- [ ] Implement form submission with loading indicator
- [ ] Handle success and error responses from API
- [ ] Implement tab switching between login/signup
- [ ] Make responsive for mobile devices
- [ ] Add accessibility attributes (labels, aria-required, etc.)
- [ ] Link from index.html to login.html
- [ ] Store CSRF token handling (if enabled)

### Testing

- [ ] Write unit tests for validation functions (email, password, name)
- [ ] Write integration tests for register endpoint
- [ ] Write integration tests for login endpoint
- [ ] Test duplicate email rejection
- [ ] Test password hashing with BCrypt
- [ ] Test response structure (no password field)
- [ ] Test error messages (generic "Invalid credentials")
- [ ] Test default role assignment
- [ ] Write property-based tests for email validation
- [ ] Write property-based tests for password strength
- [ ] Write property-based tests for uniqueness constraints
- [ ] Test session creation and validation
- [ ] Test authorization on protected endpoints

### Security

- [ ] Verify BCrypt cost factor is 10+
- [ ] Verify no plaintext passwords in logs
- [ ] Verify no plaintext passwords in responses
- [ ] Verify CSRF protection enabled (or explicitly disabled with rationale)
- [ ] Verify SQL injection prevention (parameterized queries)
- [ ] Verify XSS prevention (input sanitization, output encoding)
- [ ] Verify session cookies are secure (HttpOnly, Secure flags)
- [ ] Test authorization with invalid session
- [ ] Test HTTPS enforcement in production

---

## Class and Method Signatures

### Frontend

```javascript
class AuthForm {
    // Validation
    isValidEmail(email: string): boolean
    isValidPassword(password: string): boolean
    isValidFullName(name: string): boolean
    getPasswordRequirements(password: string): string[]
    
    // Login
    validateLoginEmail(email: string): boolean
    validateLoginPassword(password: string): boolean
    handleLoginSubmit(e: Event): Promise<void>
    
    // Signup
    validateFullName(name: string): boolean
    validateSignupEmail(email: string): boolean
    validatePassword(password: string): boolean
    validatePasswordMatch(): boolean
    validateTermsAccept(): boolean
    handleSignupSubmit(e: Event): Promise<void>
    
    // UI
    showLoginForm(): void
    showSignupForm(): void
    setupEventListeners(): void
    setupBlurValidation(): void
}
```

### Backend

```java
// UserRepository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

// AuthController
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user)
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request)
}

// SecurityConfig (unchanged)
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder()
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
}

// CustomUserDetailsService (unchanged)
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email)
}
```

---

## Deployment and Configuration

### Environment Variables

```properties
# H2 (Development)
spring.datasource.url=jdbc:h2:mem:forgefitdb
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# MySQL (Production) - example
spring.datasource.url=jdbc:mysql://localhost:3306/forgefit
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Shared
spring.jpa.hibernate.ddl-auto=update  # or 'validate' in production
server.port=8080
```

### Build and Run

```bash
# Build
mvn clean package

# Run
java -jar target/forgefit-gym-1.0.0.jar

# Access
http://localhost:8080/login.html
```

---

## Future Enhancements

1. **Token-Based Authentication (JWT)**
   - Replace session cookies with JWT tokens
   - Support mobile clients without cookie storage

2. **Email Verification**
   - Send verification email upon registration
   - Require email confirmation before account activation

3. **Password Reset**
   - Implement forgot password flow
   - Send reset link to email

4. **Two-Factor Authentication (2FA)**
   - TOTP or SMS-based second factor

5. **Membership Tiers**
   - Store membership_tier in User entity
   - Implement tier-based access control

6. **Audit Logging**
   - Log all authentication events
   - Track failed login attempts

7. **Rate Limiting**
   - Prevent brute-force attacks
   - Limit registration attempts per IP

8. **Social Login**
   - OAuth2 integration (Google, GitHub)
   - Simplified signup process

---

## References and Compliance

### Security Standards

- **OWASP Top 10:** Compliance with authentication and injection prevention
- **RFC 5322:** Email format validation
- **RFC 5321:** Email length constraints
- **BCrypt:** Password hashing algorithm (adaptive cost factor)

### Spring Framework Documentation

- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Boot: https://spring.io/projects/spring-boot

