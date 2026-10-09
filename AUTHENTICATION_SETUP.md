# ForgeFit Authentication Feature - Setup & Testing Guide

## 📋 Overview

This document provides a comprehensive guide to set up and test the newly implemented user authentication feature for ForgeFit Gym.

The feature includes:
- ✅ User registration with email uniqueness and password strength validation
- ✅ User login with Spring Security integration
- ✅ BCrypt password hashing (cost factor 10)
- ✅ Session management with single session per user
- ✅ Responsive login/signup HTML interface
- ✅ Frontend validation with error handling
- ✅ Backend input validation and error messages

---

## 🚀 Quick Start

### 1. Build the Application

```bash
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the Login Page

Navigate to: **http://localhost:8080/login.html**

---

## 📡 API Endpoints

### User Registration

**Endpoint:** `POST /api/auth/register`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

**Success Response (HTTP 200):**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "message": "Registration successful"
}
```

**Error Responses:**
- Email already exists: `{"message": "Email already exists"}`
- Weak password: `{"message": "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character"}`
- Invalid email: `{"message": "Email is required"}` (when empty) or validation error

---

### User Login

**Endpoint:** `POST /api/auth/login`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

**Success Response (HTTP 200):**
```json
{
  "message": "Login successful",
  "email": "john@example.com",
  "role": "USER"
}
```

**Error Response (HTTP 400):**
```json
{
  "message": "Invalid credentials"
}
```

Note: The error message is intentionally generic to prevent account enumeration attacks.

---

### User Logout

**Endpoint:** `POST /api/auth/logout`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json"
```

**Response:**
- Redirects to login.html
- Session is invalidated
- User must log in again

---

## 🧪 Testing Scenarios

### Test Case 1: Successful Registration

1. Navigate to **http://localhost:8080/login.html**
2. Click **"Sign Up"** tab
3. Fill in the form:
   - Full Name: `John Doe`
   - Email: `john.doe@example.com`
   - Password: `SecurePass123!`
   - Confirm Password: `SecurePass123!`
   - Check: "I accept the Terms and Conditions"
4. Click **"Sign Up"** button
5. **Expected:** Success message and redirect to login tab with email pre-filled

### Test Case 2: Successful Login

1. Fill in the login form:
   - Email: `john.doe@example.com`
   - Password: `SecurePass123!`
2. Click **"Login"** button
3. **Expected:** 
   - Success message
   - Redirect to **http://localhost:8080/dashboard.html**
   - User email and role displayed on dashboard

### Test Case 3: Weak Password Validation (Frontend)

1. Go to Sign Up tab
2. Enter Full Name: `Test User`
3. Enter Email: `test@example.com`
4. Enter Password: `weak` (only 4 characters)
5. Press Tab (blur event)
6. **Expected:** Error message: "Password must contain: 8+ characters, lowercase letter, number, special character"

### Test Case 4: Invalid Email Format

1. Go to Login tab
2. Enter Email: `invalid.email.format` (missing @domain)
3. Press Tab (blur event)
4. **Expected:** Error message: "Please enter a valid email address"

### Test Case 5: Duplicate Email Registration

1. Register user: `duplicate@example.com` (if not already registered)
2. Try to register again with same email
3. **Expected:** Error message: "Email already exists"

### Test Case 6: Invalid Login Credentials

1. Enter Email: `john.doe@example.com`
2. Enter Password: `WrongPassword123!`
3. Click "Login"
4. **Expected:** Error message: "Invalid credentials"

### Test Case 7: Password Mismatch

1. Go to Sign Up tab
2. Enter Password: `SecurePass123!`
3. Enter Confirm Password: `DifferentPass123!`
4. Press Tab on confirm password field
5. **Expected:** Error message: "Passwords do not match"

### Test Case 8: Missing Required Fields

1. Go to Login tab
2. Leave email empty
3. Click "Login"
4. **Expected:** Error message: "Email is required"

### Test Case 9: Remember Me Functionality

1. Login with email: `john.doe@example.com`
2. Check "Remember me" checkbox
3. Successfully log in (redirects to dashboard)
4. Logout
5. Return to **http://localhost:8080/login.html**
6. **Expected:** Email field is pre-filled with `john.doe@example.com`

### Test Case 10: Responsive Design

1. Open login.html in browser
2. Test on different screen sizes:
   - Desktop (1920x1080): Should display in two-column layout
   - Tablet (768px): Should display single-column
   - Mobile (375px): Should display optimized single-column
3. **Expected:** Forms remain accessible and properly aligned on all screen sizes

---

## 🔐 Password Strength Requirements

Valid password example: `SecurePass123!`

Requirements:
- ✅ Minimum 8 characters
- ✅ Maximum 128 characters
- ✅ At least one uppercase letter (A-Z)
- ✅ At least one lowercase letter (a-z)
- ✅ At least one digit (0-9)
- ✅ At least one special character (!@#$%^&*()_+-=[]{}...etc)

Invalid password examples:
- ❌ `weak` - too short, missing requirements
- ❌ `NoSpecialChar1` - missing special character
- ❌ `nouppercase1!` - missing uppercase
- ❌ `NOLOWERCASE1!` - missing lowercase
- ❌ `NoDigits!` - missing digit

---

## 🗄️ Database Verification

### Check if User is Saved

Access H2 Console: **http://localhost:8080/h2-console**

Default credentials:
- URL: `jdbc:h2:mem:forgefitdb`
- User: `sa`
- Password: (leave empty)

Query to see registered users:
```sql
SELECT * FROM users;
```

You should see:
```
ID | EMAIL                | FULL_NAME  | PASSWORD (BCrypt Hash)           | ROLE
1  | john.doe@example.com | John Doe   | $2b$10$...55+ characters...      | USER
```

---

## 📊 Session Management

### Session Behavior

1. **Single Session Per User**: If you log in from two different browsers/tabs with the same credentials, the previous session is invalidated.

2. **Session Timeout**: 
   - Idle timeout: 30 minutes (if configured)
   - Absolute timeout: 8 hours

3. **Session Invalidation on Logout**: After logout, the session cookie is deleted and authentication is required for protected endpoints.

### Test Session Management

1. Log in with `john@example.com` in **Browser 1**
2. Log in with `john@example.com` in **Browser 2**
3. Return to **Browser 1** and refresh the dashboard
4. **Expected:** Browser 1 session is invalidated, redirects to login

---

## 🔍 Security Verification

### 1. Password Hashing Verification

Register a user and check H2 console:
- Password should be a BCrypt hash: starts with `$2a$`, `$2b$`, or `$2y$`
- Hash should never equal the plaintext password
- Example hash: `$2b$10$4axnbGVqsBCamG2iIHuPnuLXJLV5DQ0WL8OKJkqbJ0YmCNm/mJ4pC`

### 2. No Plaintext Passwords in Responses

Check login/register API responses:
- Password field should NEVER appear in JSON response
- Response contains: `id`, `email`, `role`, `message` (but NO `password`)

### 3. Generic Error Messages

Test account enumeration prevention:
- Register user: `test@example.com`
- Try login with `nonexistent@example.com` and wrong password
- Both should return: `"Invalid credentials"` (no distinction)

### 4. Input Validation

Test injection prevention:
- Email with SQL keywords: `test'; DROP TABLE users; --@example.com`
- **Expected:** Validation fails, user not created
- Backend uses parameterized queries (Spring Data JPA)

---

## 📝 Logging

Check application logs for security events:

```
INFO com.forgefit.controller.AuthController - User registered successfully: john@example.com
INFO com.forgefit.controller.AuthController - User logged in successfully: john@example.com
WARN com.forgefit.controller.AuthController - Login failed for email: nonexistent@example.com (invalid credentials)
ERROR com.forgefit.controller.AuthController - Database integrity error during registration
```

---

## 🐛 Troubleshooting

### Issue: Login fails with "Invalid credentials" even with correct password

**Solution:** 
- Verify user is registered in H2 console
- Check that password hasn't been changed
- Ensure Spring Security is properly configured

### Issue: User can log in from multiple concurrent sessions

**Solution:**
- Verify `maximumSessions(1)` is configured in SecurityConfig
- Ensure Spring Session is properly initialized

### Issue: Password hash looks wrong in database

**Solution:**
- BCrypt hashes should be exactly 60 characters (with cost factor prefix)
- Format: `$2b$10$[22 char salt][31 char hash]`
- If different, password encoder might not be configured correctly

### Issue: Static resources (login.html, auth.js) not loading

**Solution:**
- Files should be in: `src/main/resources/static/`
- Ensure Spring Boot is configured to serve static content
- Check browser console for 404 errors

---

## 📚 API Documentation Reference

For more details, see:
- **Requirements:** `.kiro/specs/user-authentication-login/requirements.md`
- **Design:** `.kiro/specs/user-authentication-login/design.md`
- **Tasks:** `.kiro/specs/user-authentication-login/tasks.md`

---

## ✅ Feature Checklist

- ✅ User registration with email uniqueness
- ✅ Password strength validation
- ✅ Login with Spring Security
- ✅ BCrypt password hashing (cost 10)
- ✅ Session management
- ✅ Responsive HTML forms
- ✅ Frontend validation with error display
- ✅ Backend input validation
- ✅ Generic error messages
- ✅ CSRF protection framework
- ✅ Logout functionality
- ✅ Remember me feature

---

## 🚀 Next Steps

1. Run integration tests to verify database operations
2. Test API endpoints with curl/Postman
3. Test frontend form validation
4. Perform security testing (injection, etc.)
5. Deploy to production with CSRF enabled

---

For questions or issues, refer to the spec documents in `.kiro/specs/user-authentication-login/`
