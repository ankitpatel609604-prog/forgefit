# Requirements Document: User Authentication Login Feature

## Introduction

The ForgeFit Gym application requires a comprehensive user authentication system to enable members to access the platform securely. This document specifies the login and signup functionality, including frontend HTML forms, backend API endpoints, and security mechanisms. The system must support account creation, user login with email/password authentication, and session management for gym membership tiers (Starter, Pro, Elite).

## Glossary

- **System**: The ForgeFit authentication system including frontend login interface and backend API services
- **User**: A person registering or logging into the ForgeFit platform with a gym membership
- **Email**: User's unique electronic mail address used as login identifier
- **Password**: User's private credential for account access
- **Authentication**: The process of verifying a user's identity through email and password
- **Session**: An authenticated user's active connection to the system
- **Token**: A cryptographic artifact representing an authenticated user session (if token-based auth implemented)
- **Salt**: Random data added to passwords before hashing to prevent rainbow table attacks
- **Hash**: Cryptographic transformation of sensitive data into fixed-length irreversible output
- **CSRF**: Cross-Site Request Forgery; attack where unauthorized commands are executed on behalf of a user
- **Login Form**: HTML interface where users enter email and password to authenticate
- **Signup Form**: HTML interface where new users register with account details
- **Membership Tier**: Classification of user subscription level (Starter, Pro, Elite)
- **Role**: User's authorization level in the system (USER, ADMIN)
- **Validation**: Process of verifying user input meets defined constraints
- **API Endpoint**: HTTP route exposed by the backend for authentication operations

## Requirements

### Requirement 1: User Registration Form

**User Story:** As a new gym member, I want to register for an account on the login page, so that I can access the ForgeFit platform and manage my membership.

#### Acceptance Criteria

1. WHEN a user navigates to login.html, THE System SHALL display a signup form with fields for full name, email address, password, and password confirmation
2. WHEN a user enters their details into the signup form, THE System SHALL validate that the email field contains a valid email format (RFC 5322 compliant)
3. WHEN a user enters their details into the signup form, THE System SHALL validate that the password field contains at least 8 characters including at least one uppercase letter, one lowercase letter, one number, and one special character
4. WHEN a user's password and password confirmation fields do not match, THE System SHALL display an error message stating "Passwords do not match" without submitting the form
5. WHEN a user submits the signup form with a new email address, THE System SHALL send a POST request to /api/auth/register with the user's full name, email, and password
6. WHEN a user attempts to register with an email already in the database, THE System SHALL display an error message stating "Email already exists" and SHALL NOT create a duplicate account
7. WHEN signup is successful, THE System SHALL store the user's full name, email, and encrypted password in the database with role set to "USER"
8. WHEN signup is successful, THE System SHALL display a success message and redirect the user to the login form or dashboard

### Requirement 2: User Login Form

**User Story:** As a registered ForgeFit member, I want to log in with my email and password, so that I can access my account and training programs.

#### Acceptance Criteria

1. WHEN a user navigates to login.html, THE System SHALL display a login form with email and password fields
2. WHEN a user enters an email address into the login form, THE System SHALL validate that the input is a non-empty string before allowing submission
3. WHEN a user enters a password into the login form, THE System SHALL accept the input as masked characters (not displayed as plain text)
4. WHEN a user submits the login form with valid credentials, THE System SHALL send a POST request to /api/auth/login with email and password
5. WHEN a user provides an email address not registered in the database, THE System SHALL return an error message stating "Invalid credentials" without revealing whether the email exists
6. WHEN a user provides a password that does not match the stored hash for their email, THE System SHALL return an error message stating "Invalid credentials"
7. WHEN login is successful with valid email and password, THE System SHALL authenticate the user and establish a session
8. WHEN login is successful, THE System SHALL redirect the user to a dashboard or member portal
9. WHEN login fails, THE System SHALL remain on the login page and display an error message

### Requirement 3: Password Security and Hashing

**User Story:** As a security-conscious system owner, I want user passwords to be encrypted using industry-standard algorithms, so that user accounts remain secure even if the database is compromised.

#### Acceptance Criteria

1. WHEN a user registers or changes their password, THE System SHALL hash the password using BCrypt with a minimum cost factor of 10 before storing in the database
2. WHEN a user logs in, THE System SHALL compare the provided password against the stored BCrypt hash using constant-time comparison to prevent timing attacks
3. WHEN a password is stored in the database, THE System SHALL NOT store plain-text passwords under any circumstances
4. WHEN a user's password hash is retrieved from the database, THE System SHALL verify the hash format is valid BCrypt before authentication attempt

### Requirement 4: Form Validation and Error Handling

**User Story:** As a user providing account information, I want clear validation messages when I enter invalid data, so that I can correct my input and successfully register or log in.

#### Acceptance Criteria

1. WHEN a user submits a form with empty required fields, THE System SHALL display validation error messages for each empty field specifying which field is required
2. WHEN a user enters an email that does not match the pattern "name@domain.extension", THE System SHALL display an error message stating "Please enter a valid email address"
3. WHEN a user enters a password with fewer than 8 characters, THE System SHALL display an error message stating "Password must be at least 8 characters long"
4. WHEN a user enters a password missing required character types, THE System SHALL display an error message specifying which character types are missing (uppercase, lowercase, number, special character)
5. WHEN a registration or login API call fails due to server error, THE System SHALL display a user-friendly error message without exposing internal server details
6. WHEN form validation fails on the frontend, THE System SHALL prevent the form submission and highlight invalid fields with visual indicators (red border or error icon)
7. WHEN a user clicks outside a form field, THE System SHALL validate that field's input and display inline error messages if invalid

### Requirement 5: User Authentication Backend Service

**User Story:** As a backend system, I need to provide authentication services, so that login and registration requests are processed securely.

#### Acceptance Criteria

1. WHEN a POST request is received at /api/auth/register with valid user data, THE AuthController SHALL create a new User entity and persist it to the database
2. WHEN a POST request is received at /api/auth/login with email and password, THE AuthController SHALL authenticate using Spring Security's AuthenticationManager
3. WHEN authentication succeeds, THE AuthController SHALL return a successful HTTP 200 response with user information (email, role, but NOT password)
4. WHEN authentication fails, THE AuthController SHALL return an HTTP 400 Bad Request response without revealing whether the failure was due to invalid email or invalid password
5. WHEN a User is created during registration, THE System SHALL automatically assign the role "USER" if no role is specified
6. WHEN a user object is returned to the frontend, THE System SHALL exclude the password field from the response

### Requirement 6: Session and Authentication State Management

**User Story:** As a logged-in user, I want my authentication state to persist during my session, so that I don't have to log in repeatedly while using the application.

#### Acceptance Criteria

1. WHEN a user successfully logs in, THE System SHALL create an authenticated session within the application
2. WHEN an authenticated user makes subsequent requests, THE System SHALL validate their session and allow access to protected endpoints
3. WHEN an unauthenticated user attempts to access a protected endpoint, THE System SHALL deny access and redirect to the login page
4. WHEN a user logs out or their session expires, THE System SHALL clear the session state and require re-authentication for further access

### Requirement 7: Frontend HTML Login Interface

**User Story:** As a gym member accessing the site, I want a professional and user-friendly login page, so that I can easily navigate registration and login forms.

#### Acceptance Criteria

1. THE System SHALL provide a login.html file linked from the main index.html page
2. WHEN login.html is loaded, THE System SHALL display a professional two-column or tabbed interface with separate Login and Signup sections
3. WHEN a user views the Login section, THE System SHALL display email input field, password input field (masked), remember-me checkbox (optional), and Login button
4. WHEN a user views the Signup section, THE System SHALL display full name input, email input, password input (masked), password confirmation input (masked), terms acceptance checkbox, and Sign Up button
5. WHEN a user interacts with input fields, THE System SHALL provide clear labels for each field with associated placeholder text
6. WHEN form validation occurs, THE System SHALL display error messages in a visible location (below field or above form)
7. THE System SHALL apply consistent CSS styling matching the ForgeFit brand guidelines from styles.css
8. WHEN login.html is viewed on mobile devices, THE System SHALL display in a responsive single-column layout
9. WHEN a user clicks the Login button, THE System SHALL disable the button and display a loading indicator until the request completes

### Requirement 8: CSRF Protection

**User Story:** As a security administrator, I want the authentication system protected against cross-site request forgery attacks, so that unauthorized actors cannot perform login/logout actions on behalf of users.

#### Acceptance Criteria

1. WHEN a POST request is received to /api/auth/login or /api/auth/register, THE System SHALL validate a CSRF token if CSRF protection is enabled
2. WHEN login.html is served to a user, THE System SHALL provide a CSRF token embedded in the page or available via a preflight request
3. WHEN a form submission occurs, THE System SHALL include the CSRF token in the request headers or form data
4. WHEN a CSRF token is invalid or missing for a POST request, THE System SHALL reject the request with HTTP 403 Forbidden response

### Requirement 9: Input Validation and Sanitization

**User Story:** As a system security team, I want all user input validated and sanitized, so that the system is protected against injection attacks and malicious input.

#### Acceptance Criteria

1. WHEN user input is received for email field, THE System SHALL validate that the input does not exceed 254 characters (RFC 5321)
2. WHEN user input is received for password field, THE System SHALL validate that the input does not exceed 128 characters
3. WHEN user input is received for full name field, THE System SHALL validate that the input does not exceed 100 characters
4. WHEN user input is received from any form field, THE System SHALL strip whitespace from beginning and end of input
5. WHEN user input contains special characters, THE System SHALL sanitize the input to prevent SQL injection, XSS, or other injection attacks
6. WHEN invalid characters are detected in input fields, THE System SHALL reject the submission and display an error message

### Requirement 10: User Repository Implementation

**User Story:** As a backend system, I need database access for user data operations, so that registration and login can persist and retrieve user information.

#### Acceptance Criteria

1. THE System SHALL provide a UserRepository interface extending Spring Data JPA CrudRepository
2. WHEN the UserRepository is queried by email, THE UserRepository SHALL implement a findByEmail(String email) method returning an Optional<User>
3. WHEN a user object is saved to the database, THE System SHALL generate a unique auto-incremented ID for each user
4. WHEN a user is retrieved from the database, THE System SHALL return complete user information including id, fullName, email, password hash, and role

### Requirement 11: Successful Login Response Structure

**User Story:** As a frontend application, I need structured response data from the backend, so that I can properly update the UI and manage user state.

#### Acceptance Criteria

1. WHEN login is successful, THE System SHALL return a JSON response with message field containing "Login successful"
2. WHEN login is successful, THE System SHALL include the user's email in the response
3. WHEN login is successful, THE System SHALL include the user's role in the response
4. WHEN signup is successful, THE System SHALL return a JSON response containing id, fullName, email, and role fields
5. WHEN any authentication request fails, THE System SHALL return a JSON response with message field describing the error

### Requirement 12: Account Creation and User Onboarding

**User Story:** As a new gym member, I want a clear account creation process with optional membership tier selection, so that I can choose the right membership for my fitness goals.

#### Acceptance Criteria

1. WHEN a new user registers, THE System SHALL create their account with an initial account status of "active"
2. WHEN a new user completes registration, THE System SHALL optionally allow membership tier selection (Starter, Pro, Elite)
3. WHEN a user selects a membership tier during registration, THE System SHALL associate the tier with their User record
4. WHEN a new user account is created, THE System SHALL be ready to support future profile updates (phone, address, membership tier changes)

## Property-Based Testing Strategy

### Parsers: Round-Trip Properties (If Applicable)

- If JSON is used in authentication requests/responses, verify `parse(serialize(user)) ≈ user`

### Common Correctness Patterns

#### Invariant: Email Uniqueness
- After registration, querying the database by registered email SHALL return exactly one User record
- Attempting to register with an existing email SHALL fail without modifying the existing record

#### Invariant: Password Security
- Stored password hash SHALL never equal the plaintext password
- Two instances of the same plaintext password SHALL produce different hashes (due to salt)

#### Invariant: Role Assignment
- All new users SHALL have role "USER" after registration (unless explicitly set otherwise)
- User role retrieved from database SHALL match user role returned in login response

#### Round-Trip Property: User Creation and Retrieval
- Given user data submitted to /api/auth/register, retrieve user by email and verify all fields match input (except password is hashed)

#### Idempotence: Login Attempts
- Multiple login calls with same valid credentials SHALL return success response each time
- Failed login attempts SHALL NOT modify the user record or password hash

#### Metamorphic Property: Error Message Consistency
- "Invalid credentials" error for wrong password SHALL match "Invalid credentials" error for wrong email (no distinguishing messages)

---

## Implementation Notes

- The AuthController and SecurityConfig are already implemented; requirements ensure frontend and backend are aligned
- BCrypt password encoding is already configured in SecurityConfig
- Spring Security is configured to allow `/api/auth/**` endpoints without authentication
- UserRepository needs to be created to support findByEmail() queries
- Frontend should follow responsive design for mobile and desktop
- Password requirements (8 chars, mixed case, number, special char) ensure reasonable security posture
- Error messages intentionally vague to prevent account enumeration attacks

