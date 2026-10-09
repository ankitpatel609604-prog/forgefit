# Implementation Plan: User Authentication Login Feature

## Overview

This implementation plan converts the design and requirements into actionable coding tasks. The feature spans database schema enhancements, backend API implementation, frontend form development, security integration, and comprehensive testing. Tasks are organized into logical groups with clear dependencies to ensure incremental progress.

**Key Dependencies:**
- UserRepository must be created early (tasks 1.2)
- User entity enhancements must precede API implementation (tasks 1.1)
- Backend must be tested before frontend integration testing (tasks 2 before 5)
- Property-based tests validate correctness properties from design document

**Deliverables:**
- Enhanced User entity with proper column constraints and database indexes
- UserRepository with email lookup optimization
- Frontend login.html with responsive design and tab switching
- auth.js with comprehensive client-side validation
- Register and login API endpoints with proper error handling
- Unit, integration, and property-based tests
- Security integration (CSRF tokens, session management)

---

## Tasks

### Phase 1: Database and ORM Setup

- [ ] 1.1 Enhance User entity with column constraints and indexes
  - Add proper column annotations to User.java with constraints
  - Set email column to max 254 characters (RFC 5321 compliance)
  - Set password column to max 255 characters for BCrypt compatibility
  - Set fullName column to max 100 characters
  - Add @Column(name = "full_name") for database naming
  - Add database indexes on email and role columns using @Index annotations
  - Verify auto-increment strategy is IDENTITY for H2 compatibility
  - Add created_at timestamp column (optional, for future audit logging)
  - _Requirements: 5.1, 10.1, 10.3_

- [ ] 1.2 Create UserRepository interface
  - Create new file: src/main/java/com/forgefit/repository/UserRepository.java
  - Extend JpaRepository<User, Long>
  - Implement findByEmail(String email) method returning Optional<User>
  - Add Javadoc explaining the method purpose and RFC compliance
  - Ensure repository is properly scanned by Spring
  - _Requirements: 10.1, 10.2_

- [ ] 1.3 Verify User entity and repository database operations
  - Write integration test to verify User can be saved and retrieved from database
  - Test that email uniqueness constraint is enforced at database level
  - Test findByEmail() returns correct user
  - Test findByEmail() returns empty Optional for non-existent email
  - Run test and verify database schema is created correctly
  - _Requirements: 10.3, 10.4_

### Phase 2: Backend API Implementation

- [ ] 2.1 Create request/response DTOs for authentication
  - Create RegisterRequest DTO with fullName, email, password fields
  - Create LoginRequest DTO with email, password fields
  - Create AuthResponse DTO with id, fullName, email, role fields (no password)
  - Add Jakarta validation annotations (@NotNull, @NotBlank, @Email, @Size)
  - Add Javadoc to each DTO class
  - _Requirements: 5.1, 5.3, 5.6, 11.4_

- [ ] 2.2 Implement input validation in AuthController.register()
  - Validate fullName is not null, not blank, and max 100 characters
  - Validate email is not null, not blank, max 254 characters, and valid RFC 5322 format
  - Validate password is not null, not blank, and max 128 characters
  - Trim all string inputs before processing
  - Check email exists in database (case-insensitive)
  - Return 400 Bad Request with "Email already exists" if duplicate found
  - Return appropriate validation errors for invalid input
  - Hash password using PasswordEncoder before saving
  - Set default role to "USER" if not specified
  - _Requirements: 1.6, 4.1, 4.2, 4.3, 4.4, 5.1, 5.5, 9.1, 9.2, 9.3, 9.4_

- [ ] 2.3 Implement input validation in AuthController.login()
  - Validate email is not null, not blank
  - Validate password is not null, not blank
  - Trim email input before processing
  - Use AuthenticationManager to authenticate (Spring Security integration)
  - Return "Invalid credentials" for both wrong email and wrong password (no distinction)
  - Return HTTP 200 with success response for valid login
  - Return HTTP 400 with error message for invalid login
  - Ensure password is never included in response body
  - _Requirements: 2.2, 2.3, 2.5, 2.6, 5.2, 5.3, 5.6_

- [ ] 2.4 Ensure password hashing with BCrypt in register()
  - Verify PasswordEncoder bean uses BCryptPasswordEncoder with cost factor 10+
  - Encode password before saving user to database
  - Verify password hash format ($2a$, $2b$, or $2y$ prefix with 53+ additional chars)
  - Write test to verify plaintext password never stored
  - Write test to verify same plaintext produces different hashes (due to salt)
  - _Requirements: 3.1, 3.3, 3.4_

- [ ] 2.5 Enhance error handling in AuthController
  - Catch database constraint violations (duplicate email)
  - Catch validation errors from DTO constraints
  - Catch authentication exceptions from AuthenticationManager
  - Return user-friendly error messages without stack traces or internal details
  - Return consistent HTTP status codes (400 for client error, 500 for server error)
  - Log errors server-side for debugging
  - _Requirements: 4.5, 4.6, 4.7, 5.4_

- [ ] 2.6 Implement SecurityConfig enhancements for session management
  - Verify CSRF protection configuration (currently disabled, document rationale)
  - Configure session cookie attributes (Secure, HttpOnly, SameSite=Strict)
  - Configure authentication entry point for 401 responses
  - Allow anonymous access to /api/auth/**, /login.html, /index.html
  - Require authentication for all other endpoints
  - Configure logout behavior (session invalidation)
  - _Requirements: 6.1, 6.3, 6.4, 8.1, 8.4_

- [ ] 2.7 Checkpoint - Backend API functional
  - Run all backend tests and verify they pass
  - Test register endpoint with valid/invalid inputs using curl or Postman
  - Test login endpoint with valid/invalid credentials
  - Verify responses have correct structure (no password field)
  - Verify error messages are generic (no information leakage)
  - Ask user if there are questions or blockers before proceeding

### Phase 3: Frontend Implementation - Login Page Structure

- [ ] 3.1 Create login.html with responsive layout
  - Create new file: src/main/resources/static/login.html
  - Include proper DOCTYPE, meta tags (charset, viewport, description)
  - Link to styles.css for consistent branding
  - Create auth-container div with two sections: login-section and signup-section
  - Implement tab/section switching UI (tabbed or two-column layout)
  - Add navigation bar with link back to index.html
  - Add CSS for responsive mobile design (single column)
  - Add loading indicator spinners and messages
  - Include CSRF token placeholder input (comment for future use)
  - _Requirements: 7.1, 7.2, 7.7, 7.8_

- [ ] 3.2 Implement login form HTML structure
  - Create form with id="loginForm", method POST (handled by JavaScript)
  - Add email input with proper attributes (type="email", placeholder, aria-required, autocomplete="email")
  - Add password input with masked display (type="password", autocomplete="current-password")
  - Add "Remember me" checkbox (optional feature)
  - Add Login button with id="loginBtn"
  - Add error message containers (loginEmailError, loginPasswordError, loginFormError)
  - Add success message container (loginFormSuccess)
  - Add novalidate attribute to prevent browser validation conflicts
  - Add links to switch between login and signup forms
  - _Requirements: 2.1, 2.3, 7.3, 7.5, 7.9_

- [ ] 3.3 Implement signup form HTML structure
  - Create form with id="signupForm"
  - Add fullName input with maxlength="100", aria-required="true"
  - Add email input with maxlength="254", aria-required="true"
  - Add password input with maxlength="128", placeholder showing requirements
  - Add passwordConfirm input for password verification
  - Add password requirements helper text
  - Add terms acceptance checkbox (required)
  - Add Sign Up button with id="signupBtn"
  - Add error message containers for each field
  - Add form-level error and success message containers
  - Add links to switch between login and signup forms
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 7.4, 7.5, 7.6_

- [ ] 3.4 Create auth.js with AuthForm class structure
  - Create new file: src/main/resources/static/auth.js
  - Implement AuthForm class with constructor
  - Initialize form references in constructor (loginForm, signupForm, etc.)
  - Implement setupEventListeners() method
  - Implement setupBlurValidation() method for real-time validation
  - Add methods for showing/hiding login and signup sections
  - Document all methods with JSDoc comments
  - Export AuthForm for browser DOMContentLoaded initialization
  - _Requirements: 1.1, 2.1, 7.5_

### Phase 4: Frontend Validation Logic - JavaScript

- [ ] 4.1 Implement email validation function
  - **Property 1: Email Format Validation (validates Requirement 1.2)**
  - Implement isValidEmail(email) returning boolean
  - Use RFC 5322 compliant regex pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  - Check email max length (254 characters)
  - Validate both standard and complex email formats (user+tag@sub.domain.co.uk)
  - Reject emails with special characters at start/end
  - _Requirements: 1.2, 4.2, 9.1_

- [ ] 4.2 Implement password strength validation function
  - **Property 2: Password Strength Enforcement (validates Requirement 1.3)**
  - Implement isValidPassword(password) returning boolean
  - Check minimum length (8 characters)
  - Check maximum length (128 characters)
  - Check for at least one uppercase letter (/[A-Z]/)
  - Check for at least one lowercase letter (/[a-z]/)
  - Check for at least one digit (/\d/)
  - Check for at least one special character (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/)
  - Implement getPasswordRequirements(password) returning array of missing types
  - Return specific feedback for each missing requirement
  - _Requirements: 1.3, 4.3, 4.4, 9.2_

- [ ] 4.3 Implement full name validation function
  - **Property 11: Full Name Max Length Validation (validates Requirement 9.3)**
  - Implement isValidFullName(name) returning boolean
  - Check that name is not empty after trimming
  - Check that name does not exceed 100 characters
  - Return specific error message for length violations
  - _Requirements: 1.1, 9.3_

- [ ] 4.4 Implement login form field validators
  - Implement validateLoginEmail(email) with error display
  - Implement validateLoginPassword(password) with error display
  - Show "Email is required" error if empty
  - Show "Please enter a valid email address" error if invalid format
  - Show "Email must not exceed 254 characters" if too long
  - Show "Password is required" error if empty
  - Display errors below respective input fields
  - _Requirements: 2.2, 4.1, 4.2, 4.7_

- [ ] 4.5 Implement signup form field validators
  - Implement validateFullName(name) with error display
  - Implement validateSignupEmail(email) with error display
  - Implement validatePassword(password) with detailed requirements feedback
  - Implement validatePasswordMatch() comparing password and passwordConfirm
  - Implement validateTermsAccept() checking checkbox
  - Show specific error messages for each validation failure
  - Clear error messages when validation passes
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 4.1, 4.2, 4.3, 4.4_

- [ ] 4.6 Implement real-time blur validation
  - Add blur event listeners to all form fields
  - Call appropriate validation function on blur
  - Display inline error messages immediately
  - Clear error messages when field is valid
  - Prevent form submission if any field has error on blur
  - _Requirements: 4.7_

- [ ] 4.7 Checkpoint - Frontend validation working
  - Test email validation with valid and invalid formats
  - Test password validation with various strength levels
  - Test password confirmation matching
  - Test full name length constraints
  - Test error messages display correctly
  - Test that form fields can be corrected and errors clear
  - Ask user if there are questions before API integration

### Phase 5: Frontend API Integration

- [ ] 5.1 Implement handleLoginSubmit() method
  - Validate all login fields before submission
  - Return early if validation fails (prevent API call)
  - Disable login button and show loading indicator
  - Make POST request to /api/auth/login with {email, password}
  - Include Content-Type: application/json header
  - Include CSRF token in X-CSRF-Token header (if needed)
  - Handle success response (HTTP 200)
  - Handle error response (HTTP 400)
  - Store user email in localStorage (optional)
  - Redirect to /dashboard or refresh page on success
  - Display error message on failure
  - Re-enable login button in finally block
  - _Requirements: 2.4, 5.6, 6.1, 11.1_

- [ ] 5.2 Implement handleSignupSubmit() method
  - Validate all signup fields before submission
  - Return early if validation fails (prevent API call)
  - Disable signup button and show loading indicator
  - Make POST request to /api/auth/register with {fullName, email, password}
  - Include Content-Type: application/json header
  - Include CSRF token in X-CSRF-Token header (if needed)
  - Handle success response (HTTP 200) with user data
  - Handle error response (HTTP 400) including "Email already exists"
  - Display success message and redirect to login form
  - Pre-fill email field in login form after redirect
  - Display error message on failure
  - Re-enable signup button in finally block
  - _Requirements: 1.5, 1.6, 1.8, 5.6, 11.4_

- [ ] 5.3 Implement tab/section switching UI
  - Implement showLoginForm() method
  - Implement showSignupForm() method
  - Add click event listeners to tab switches
  - Prevent default navigation when clicking tab links
  - Toggle display property between login and signup sections
  - Reset forms and clear error messages when switching
  - Reset loading indicators when switching
  - _Requirements: 7.2_

- [ ] 5.4 Add form attribute helpers
  - Implement getCsrfToken() helper to extract CSRF token from page
  - Implement getFormData(formElement) helper to serialize form data
  - Implement displayError(elementId, message) to show error
  - Implement clearError(elementId) to clear error
  - Implement disableButton(buttonId, loading = true) to lock submission
  - Implement enableButton(buttonId, loading = false) to unlock submission
  - _Requirements: 8.3_

- [ ] 5.5 Checkpoint - API integration functional
  - Test successful login flow end-to-end
  - Test successful signup flow end-to-end
  - Test login with invalid credentials
  - Test signup with duplicate email
  - Test form shows appropriate error messages
  - Test button disable/enable during request
  - Test loading indicators appear and disappear
  - Ask user if there are blockers before comprehensive testing

### Phase 6: Input Sanitization and Security

- [ ] 6.1 Implement input sanitization in frontend
  - **Property 13: Input Sanitization Against Injection (validates Requirement 9.5, 9.6)**
  - Create sanitizeInput(input) function to strip whitespace
  - Create removeSpecialChars(input) to sanitize dangerous characters (optional)
  - Apply sanitization to all form fields before submission
  - Apply sanitization to all fields in handleLoginSubmit() and handleSignupSubmit()
  - Verify trimmed values are used in API requests
  - Verify trimmed values are used in error checking
  - _Requirements: 9.4, 9.5, 9.6_

- [ ] 6.2 Verify CSRF token integration (if enabled)
  - Extract CSRF token from HTML input or meta tag
  - Include token in all POST requests (header or form data)
  - Test CSRF protection prevents unauthorized requests
  - Document CSRF configuration (currently disabled in SecurityConfig)
  - _Requirements: 8.2, 8.3_

- [ ] 6.3 Implement response sanitization
  - Ensure no sensitive data (password hashes) in localStorage
  - Ensure email stored in localStorage, not password
  - Clear sensitive data on logout
  - Verify API responses never include password field
  - _Requirements: 5.3, 5.6_

### Phase 7: Testing - Unit and Property-Based Tests

- [ ] 7.1 Write unit tests for email validation
  - **Property 1: Email Format Validation**
  - **Validates: Requirements 1.2, 4.2**
  - Create test class AuthValidationTests.java
  - Test valid email formats pass: standard@domain.com, user+tag@sub.example.co.uk
  - Test invalid formats fail: missing@, @domain, user@, invalid.email
  - Test RFC 5322 compliance
  - Test max length constraint (254 characters)
  - _Requirements: 1.2, 4.2, 9.1_

- [ ] 7.2 Write unit tests for password strength validation
  - **Property 2: Password Strength Enforcement**
  - **Validates: Requirements 1.3, 4.3, 4.4**
  - Test strong password passes: "SecurePass123!"
  - Test weak passwords fail: "weak", "NoSpecial1", "nouppercase1!", "NOLOWERCASE1!"
  - Test minimum length enforcement (8 characters)
  - Test maximum length enforcement (128 characters)
  - Test all character type requirements
  - Test getPasswordRequirements() returns correct missing types
  - _Requirements: 1.3, 4.3, 4.4, 9.2_

- [ ] 7.3 Write unit tests for full name validation
  - **Property 11: Full Name Max Length Validation**
  - **Validates: Requirements 9.3**
  - Test valid names: "John Doe", "Jane Smith"
  - Test empty name fails: "", "   "
  - Test max length constraint (100 characters)
  - Test names over 100 characters fail
  - _Requirements: 1.1, 9.3_

- [ ] 7.4 Write property-based tests for email validation
  - **Property 1: Email Format Validation (PBT)**
  - **Validates: Requirements 1.2**
  - Use property framework (QuickCheck, Hypothesis, or fast-check equivalent in Java)
  - Generate arbitrary valid emails and test isValidEmail() returns true
  - Generate arbitrary invalid emails and test isValidEmail() returns false
  - Run minimum 100 iterations
  - Test round-trip: parse valid email → format → parse
  - _Requirements: 1.2, 4.2_

- [ ] 7.5 Write property-based tests for password strength
  - **Property 2: Password Strength Enforcement (PBT)**
  - **Validates: Requirements 1.3**
  - Generate strings <8 characters and verify they fail validation
  - Generate strings without uppercase and verify they fail
  - Generate strings without lowercase and verify they fail
  - Generate strings without digits and verify they fail
  - Generate strings without special characters and verify they fail
  - Generate strings with all requirements and verify they pass
  - Run minimum 100 iterations per property
  - _Requirements: 1.3, 4.3, 4.4_

- [ ] 7.6* Write unit tests for password max length validation
  - **Property 10: Password Max Length Validation (validates Requirement 9.2)**
  - Test password exactly 128 characters passes validation
  - Test password 129 characters fails validation
  - Test error message states "Password must not exceed 128 characters"
  - _Requirements: 9.2_

- [ ] 7.7* Write unit tests for email max length validation
  - **Property 9: Email Max Length Validation (validates Requirement 9.1)**
  - Test email exactly 254 characters passes validation
  - Test email 255 characters fails validation
  - Test error message states "Email must not exceed 254 characters"
  - _Requirements: 9.1_

- [ ] 7.8* Write unit tests for whitespace trimming
  - **Property 12: Whitespace Trimming (validates Requirement 9.4)**
  - Test email with leading spaces is trimmed before validation
  - Test email with trailing spaces is trimmed before validation
  - Test password with leading/trailing spaces is trimmed
  - Test full name with spaces is trimmed before storage
  - Verify trimmed values used in database queries
  - _Requirements: 9.4_

### Phase 8: Testing - Integration Tests

- [ ] 8.1 Write integration tests for registration endpoint
  - Create test class AuthControllerIntegrationTests.java
  - Test successful registration with valid data
  - Test response contains id, fullName, email, role (no password)
  - Test user is saved to database with correct values
  - Test role defaults to "USER" when not specified
  - _Requirements: 5.1, 5.5, 10.4, 11.4_

- [ ] 8.2 Write integration tests for email uniqueness constraint
  - **Property 3: Email Uniqueness Constraint (validates Requirement 1.6)**
  - **Validates: Requirements 1.6**
  - Register first user successfully
  - Attempt to register second user with same email
  - Verify registration fails with HTTP 400
  - Verify error message is "Email already exists"
  - Verify original user record unchanged
  - Verify no duplicate user created
  - _Requirements: 1.6_

- [ ] 8.3 Write integration tests for login endpoint
  - Test login with valid email and password returns HTTP 200
  - Test login response contains email, role, and success message
  - Test login response does NOT contain password field
  - Test login with non-existent email returns HTTP 400 with "Invalid credentials"
  - Test login with wrong password returns HTTP 400 with "Invalid credentials"
  - Verify both error cases return identical error message (no distinction)
  - _Requirements: 2.4, 2.5, 2.6, 5.3, 5.4, 11.1, 11.2_

- [ ] 8.4 Write integration tests for password hashing
  - **Property 5: Password Hash Security - No Plaintext Storage (validates Requirement 3.1)**
  - **Validates: Requirements 3.1, 3.3**
  - Register user with password "TestPass123!"
  - Query database and verify stored password is BCrypt hash
  - Verify stored password does not equal plaintext
  - Verify hash format starts with $2a$, $2b$, or $2y$
  - Register second user with same password
  - Verify hashes are different (due to salt)
  - _Requirements: 3.1, 3.3, 3.4_

- [ ] 8.5 Write integration tests for default user role assignment
  - **Property 4: Default User Role Assignment (validates Requirement 1.7)**
  - **Validates: Requirements 1.7, 5.5**
  - Register user without specifying role
  - Verify user.role = "USER" in database
  - Verify user.role = "USER" in response
  - Register multiple users and verify all have role "USER"
  - _Requirements: 1.7, 5.5_

- [ ] 8.6 Write integration tests for response structure
  - **Property 16: Successful Login Response Structure (validates Requirement 11.1)**
  - **Validates: Requirements 11.1, 11.2, 11.3**
  - Test login response JSON contains: message, email, role (no password)
  - Test message value is "Login successful"
  - Test email matches login request
  - Test role value is "USER"
  - _Requirements: 11.1, 11.2, 11.3_

- [ ] 8.7 Write integration tests for registration response structure
  - **Property 17: Successful Registration Response Structure (validates Requirement 11.4)**
  - **Validates: Requirements 11.4**
  - Test registration response contains: id, fullName, email, role (no password)
  - Test id is positive integer
  - Test fullName matches submitted value
  - Test email matches submitted value
  - Test role is "USER"
  - _Requirements: 11.4_

- [ ] 8.8* Write integration tests for error message consistency
  - **Property 6: Login Error Message Consistency (validates Requirement 2.5, 2.6)**
  - **Validates: Requirements 2.5, 2.6**
  - Test login with unregistered email returns "Invalid credentials"
  - Test login with wrong password returns identical "Invalid credentials" message
  - Verify no distinction between email/password failures
  - Run multiple iterations to ensure message consistency
  - _Requirements: 2.5, 2.6_

- [ ] 8.9* Write integration tests for round-trip user creation and retrieval
  - **Property 15: Complete User Retrieval Round-Trip (validates Requirement 10.4)**
  - **Validates: Requirements 10.4**
  - Submit registration request with specific values
  - Query database using UserRepository.findByEmail()
  - Verify all fields match submitted values (except password is hashed)
  - Verify password is BCrypt hash, not plaintext
  - _Requirements: 10.4_

### Phase 9: Testing - Frontend and Security

- [ ] 9.1 Write end-to-end frontend tests
  - Create test file for login.html interactions
  - Test successful signup flow: fill form → submit → redirect to login
  - Test successful login flow: fill form → submit → redirect to dashboard
  - Test form validation displays errors before submission
  - Test error messages clear when field is corrected
  - Test tab switching between login and signup
  - Test loading indicators appear and disappear
  - _Requirements: 1.8, 2.8, 2.9_

- [ ] 9.2 Write security tests for injection prevention
  - **Property 13: Input Sanitization Against Injection (validates Requirement 9.5)**
  - **Validates: Requirements 9.5, 9.6**
  - Test SQL injection in email field: `"; DROP TABLE users; --`
  - Test SQL injection in password: `' OR '1'='1`
  - Test XSS in full name field: `<script>alert('xss')</script>`
  - Test XSS in email: `user@example.com<script>alert('xss')</script>`
  - Verify all special characters are sanitized/parameterized
  - Verify user input cannot modify database schema
  - _Requirements: 9.5, 9.6_

- [ ] 9.3* Write security tests for authorization
  - Test unauthenticated user cannot access protected endpoints
  - Test authenticated user can access protected endpoints
  - Test invalid/expired session denies access
  - Test logout clears session and requires re-authentication
  - _Requirements: 6.2, 6.3, 6.4_

- [ ] 9.4* Write security tests for session management
  - Test session created after successful login
  - Test session cookie has Secure flag (HTTPS only)
  - Test session cookie has HttpOnly flag (no JavaScript access)
  - Test multiple login attempts create separate sessions
  - Test logout invalidates session
  - _Requirements: 6.1, 6.2, 6.4_

- [ ] 9.5* Write security tests for CSRF protection
  - Test CSRF token is included in responses
  - Test POST requests without CSRF token are rejected (if enabled)
  - Test invalid CSRF token is rejected
  - Test valid CSRF token allows request to proceed
  - Document CSRF configuration in SecurityConfig
  - _Requirements: 8.1, 8.2, 8.3, 8.4_

### Phase 10: Final Integration and Checkpoint

- [ ] 10.1 Link login.html from index.html and verify navigation
  - Verify login link in index.html navigates to login.html
  - Verify "Back to ForgeFit" link in login.html navigates to index.html
  - Test navigation works on desktop and mobile
  - _Requirements: 7.1_

- [ ] 10.2 Test responsive design on mobile devices
  - Test login.html on mobile viewport (375px width)
  - Verify single-column layout on mobile
  - Verify form inputs are touch-friendly (large enough)
  - Verify buttons are clickable without zooming
  - Test on tablet viewport (768px width)
  - _Requirements: 7.8_

- [ ] 10.3 Final comprehensive test execution
  - Run all unit tests and verify pass
  - Run all integration tests and verify pass
  - Run all property-based tests and verify pass
  - Run security tests and verify no vulnerabilities
  - Test end-to-end user flows (signup → login → authenticated)
  - Verify code coverage is adequate (>80% for critical paths)
  - Ask user if there are final questions or concerns

- [ ] 10.4 Final checkpoint - Ready for deployment
  - All tests passing locally
  - Code review complete
  - No console errors or warnings
  - Database migrations applied (if using production DB)
  - Performance acceptable (login < 200ms, registration < 500ms)
  - Security scan complete (no OWASP vulnerabilities)
  - Documentation updated in README
  - Ask user to approve for deployment

---

## Notes

- Tasks marked with `*` are optional and can be skipped for MVP (faster delivery)
- Each task references specific requirements for full traceability
- Property-based tests validate universal correctness properties from design document
- Optional test tasks (7.6, 7.7, 7.8, 8.8, 8.9, 9.3, 9.4, 9.5) provide comprehensive coverage but can be deferred
- Core implementation tasks (1.x, 2.x, 3.x, 4.x, 5.x) must be completed
- Frontend validation is client-side only; backend validation must also occur (defense in depth)
- CSRF protection is currently disabled in SecurityConfig; enable in production with proper token handling
- Test dependencies: unit tests are independent, integration tests require backend running, end-to-end tests require both frontend and backend

---

## Task Dependency Graph

```json
{
  "waves": [
    {
      "id": 0,
      "tasks": [
        "1.1",
        "1.2"
      ]
    },
    {
      "id": 1,
      "tasks": [
        "1.3",
        "2.1"
      ]
    },
    {
      "id": 2,
      "tasks": [
        "2.2",
        "2.3",
        "2.4",
        "2.5"
      ]
    },
    {
      "id": 3,
      "tasks": [
        "2.6",
        "3.1"
      ]
    },
    {
      "id": 4,
      "tasks": [
        "3.2",
        "3.3",
        "3.4"
      ]
    },
    {
      "id": 5,
      "tasks": [
        "4.1",
        "4.2",
        "4.3"
      ]
    },
    {
      "id": 6,
      "tasks": [
        "4.4",
        "4.5",
        "4.6"
      ]
    },
    {
      "id": 7,
      "tasks": [
        "5.1",
        "5.2",
        "5.3",
        "5.4"
      ]
    },
    {
      "id": 8,
      "tasks": [
        "6.1",
        "6.2",
        "6.3"
      ]
    },
    {
      "id": 9,
      "tasks": [
        "7.1",
        "7.2",
        "7.3",
        "7.4",
        "7.5",
        "7.6",
        "7.7",
        "7.8"
      ]
    },
    {
      "id": 10,
      "tasks": [
        "8.1",
        "8.2",
        "8.3",
        "8.4",
        "8.5",
        "8.6",
        "8.7",
        "8.8",
        "8.9"
      ]
    },
    {
      "id": 11,
      "tasks": [
        "9.1",
        "9.2",
        "9.3",
        "9.4",
        "9.5"
      ]
    },
    {
      "id": 12,
      "tasks": [
        "10.1",
        "10.2",
        "10.3",
        "10.4"
      ]
    }
  ]
}
```

---

## Implementation Sequence Summary

**Wave 0 (Foundation):** Database schema - User entity and UserRepository setup
**Wave 1 (Verification):** Test database operations before moving to backend
**Wave 2-3 (Backend APIs):** Register/login endpoints, validation, error handling, security
**Wave 4-7 (Frontend):** HTML structure, form fields, JavaScript validation, API integration
**Wave 8-9 (Testing):** Unit tests, property-based tests for correctness properties
**Wave 10-11 (Security & E2E):** Integration tests, security testing, end-to-end flows
**Wave 12 (Finalization):** Responsive design, navigation, comprehensive testing, deployment readiness

**Critical Path:** 1.1 → 1.2 → 1.3 → 2.x (backend) → 3.x (frontend HTML) → 4.x (validation) → 5.x (API integration) → 7.x & 8.x (testing)

**Estimated Time by Phase:**
- Phase 1 (Database): 1-2 hours
- Phase 2 (Backend API): 3-4 hours
- Phase 3 (Frontend HTML): 2-3 hours
- Phase 4 (Frontend Validation): 3-4 hours
- Phase 5 (Frontend API Integration): 2-3 hours
- Phase 6 (Security): 1-2 hours
- Phase 7-9 (Testing): 4-6 hours
- Phase 10 (Final): 1-2 hours

**Total Estimated: 17-26 hours for complete implementation with comprehensive testing**

