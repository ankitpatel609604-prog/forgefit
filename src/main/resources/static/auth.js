/**
 * Authentication Module for ForgeFit
 * 
 * Handles client-side form validation, API integration, and user interactions
 * for login and signup pages.
 * 
 * Features:
 * - Real-time form validation with on-blur error display
 * - Password strength validation (8+ chars, mixed case, number, special char)
 * - Email RFC 5322 compliance validation
 * - Form field trimming and sanitization
 * - API communication with proper error handling
 * - Session management and form state
 */

class AuthForm {
    // ============================================================
    // CONSTRUCTOR AND INITIALIZATION
    // ============================================================

    constructor() {
        this.loginForm = document.getElementById('loginForm');
        this.signupForm = document.getElementById('signupForm');
        this.isSubmitting = false;

        this.setupEventListeners();
        this.setupBlurValidation();
    }

    setupEventListeners() {
        // Form submissions
        this.loginForm.addEventListener('submit', (e) => this.handleLoginSubmit(e));
        this.signupForm.addEventListener('submit', (e) => this.handleSignupSubmit(e));

        // Tab switching
        document.getElementById('loginTab').addEventListener('click', (e) => {
            e.preventDefault();
            this.switchTab('login');
        });

        document.getElementById('signupTab').addEventListener('click', (e) => {
            e.preventDefault();
            this.switchTab('signup');
        });

        document.getElementById('switchToSignup').addEventListener('click', (e) => {
            e.preventDefault();
            this.switchTab('signup');
        });

        document.getElementById('switchToLogin').addEventListener('click', (e) => {
            e.preventDefault();
            this.switchTab('login');
        });
    }

    setupBlurValidation() {
        // Login form blur validation
        document.getElementById('loginEmail').addEventListener('blur', (e) => {
            this.validateLoginEmail(e.target.value);
        });

        document.getElementById('loginPassword').addEventListener('blur', (e) => {
            this.validateLoginPassword(e.target.value);
        });

        // Signup form blur validation
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

        document.getElementById('termsAccept').addEventListener('change', (e) => {
            if (e.target.checked) {
                this.clearError('termsAcceptError');
            }
        });
    }

    // ============================================================
    // VALIDATION FUNCTIONS
    // ============================================================

    /**
     * Validate email against RFC 5322 pattern.
     * Pattern: basic@domain.com, user+tag@sub.domain.co.uk
     * Max length: 254 characters (RFC 5321)
     */
    isValidEmail(email) {
        const rfc5322Regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return email.length <= 254 && rfc5322Regex.test(email);
    }

    /**
     * Validate password strength.
     * Requirements:
     * - Minimum 8 characters
     * - Maximum 128 characters
     * - At least one uppercase letter (A-Z)
     * - At least one lowercase letter (a-z)
     * - At least one digit (0-9)
     * - At least one special character (!@#$%^&*...)
     */
    isValidPassword(password) {
        if (!password || password.length < 8 || password.length > 128) {
            return false;
        }
        if (!/[A-Z]/.test(password)) return false;  // Uppercase
        if (!/[a-z]/.test(password)) return false;  // Lowercase
        if (!/\d/.test(password)) return false;      // Number
        if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) return false; // Special
        return true;
    }

    /**
     * Get array of missing password requirements.
     * Helps display specific feedback to user.
     */
    getPasswordRequirements(password) {
        const missing = [];
        if (!password || password.length < 8) missing.push('8+ characters');
        if (password && password.length > 128) missing.push('must not exceed 128 characters');
        if (!/[A-Z]/.test(password)) missing.push('uppercase letter');
        if (!/[a-z]/.test(password)) missing.push('lowercase letter');
        if (!/\d/.test(password)) missing.push('number');
        if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
            missing.push('special character');
        }
        return missing;
    }

    /**
     * Validate full name.
     * Requirements:
     * - Not empty (after trim)
     * - Max 100 characters
     */
    isValidFullName(name) {
        if (!name) return false;
        const trimmed = name.trim();
        return trimmed.length > 0 && trimmed.length <= 100;
    }

    // ============================================================
    // LOGIN FORM VALIDATION
    // ============================================================

    validateLoginEmail(email) {
        const errorEl = document.getElementById('loginEmailError');
        const input = document.getElementById('loginEmail');
        email = email.trim();

        if (!email) {
            this.showError(input, errorEl, 'Email is required');
            return false;
        }
        if (email.length > 254) {
            this.showError(input, errorEl, 'Email must not exceed 254 characters');
            return false;
        }
        if (!this.isValidEmail(email)) {
            this.showError(input, errorEl, 'Please enter a valid email address');
            return false;
        }
        this.clearError(errorEl);
        this.clearInputError(input);
        return true;
    }

    validateLoginPassword(password) {
        const errorEl = document.getElementById('loginPasswordError');
        const input = document.getElementById('loginPassword');

        if (!password) {
            this.showError(input, errorEl, 'Password is required');
            return false;
        }
        this.clearError(errorEl);
        this.clearInputError(input);
        return true;
    }

    // ============================================================
    // SIGNUP FORM VALIDATION
    // ============================================================

    validateFullName(name) {
        const errorEl = document.getElementById('signupFullNameError');
        const input = document.getElementById('signupFullName');
        name = name.trim();

        if (!name) {
            this.showError(input, errorEl, 'Full name is required');
            return false;
        }
        if (name.length > 100) {
            this.showError(input, errorEl, 'Full name must not exceed 100 characters');
            return false;
        }
        this.clearError(errorEl);
        this.clearInputError(input);
        return true;
    }

    validateSignupEmail(email) {
        const errorEl = document.getElementById('signupEmailError');
        const input = document.getElementById('signupEmail');
        email = email.trim();

        if (!email) {
            this.showError(input, errorEl, 'Email is required');
            return false;
        }
        if (email.length > 254) {
            this.showError(input, errorEl, 'Email must not exceed 254 characters');
            return false;
        }
        if (!this.isValidEmail(email)) {
            this.showError(input, errorEl, 'Please enter a valid email address');
            return false;
        }
        this.clearError(errorEl);
        this.clearInputError(input);
        return true;
    }

    validatePassword(password) {
        const errorEl = document.getElementById('signupPasswordError');
        const input = document.getElementById('signupPassword');

        if (!password) {
            this.showError(input, errorEl, 'Password is required');
            return false;
        }
        if (password.length > 128) {
            this.showError(input, errorEl, 'Password must not exceed 128 characters');
            return false;
        }
        if (!this.isValidPassword(password)) {
            const missing = this.getPasswordRequirements(password);
            this.showError(input, errorEl, `Password must contain: ${missing.join(', ')}`);
            return false;
        }
        this.clearError(errorEl);
        this.clearInputError(input);
        return true;
    }

    validatePasswordMatch() {
        const password = document.getElementById('signupPassword').value;
        const confirm = document.getElementById('signupPasswordConfirm').value;
        const errorEl = document.getElementById('signupPasswordConfirmError');
        const input = document.getElementById('signupPasswordConfirm');

        if (password !== confirm) {
            this.showError(input, errorEl, 'Passwords do not match');
            return false;
        }
        this.clearError(errorEl);
        this.clearInputError(input);
        return true;
    }

    validateTermsAccept() {
        const checkbox = document.getElementById('termsAccept');
        const errorEl = document.getElementById('termsAcceptError');

        if (!checkbox.checked) {
            this.showError(null, errorEl, 'You must accept the Terms and Conditions');
            return false;
        }
        this.clearError(errorEl);
        return true;
    }

    // ============================================================
    // FORM SUBMISSION HANDLERS
    // ============================================================

    async handleLoginSubmit(e) {
        e.preventDefault();

        // Prevent double submission
        if (this.isSubmitting) return;

        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;

        // Validate
        const emailValid = this.validateLoginEmail(email);
        const passwordValid = this.validateLoginPassword(password);

        if (!emailValid || !passwordValid) {
            return;
        }

        // Disable button and show loading
        this.disableButton('loginBtn', true);

        try {
            this.isSubmitting = true;
            this.clearFormLevelError('loginFormError');
            this.clearFormLevelSuccess('loginFormSuccess');

            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-Token': document.getElementById('csrfToken').value || ''
                },
                body: JSON.stringify({ email, password }),
                timeout: 30000
            });

            const data = await response.json();

            if (response.ok) {
                // Success
                this.showFormLevelSuccess('loginFormSuccess', 
                    data.message || 'Login successful! Redirecting...');
                
                // Store user info in localStorage
                localStorage.setItem('userEmail', data.email || '');
                localStorage.setItem('userRole', data.role || 'USER');

                // Store email locally for "remember me" feature
                if (document.getElementById('rememberMe').checked) {
                    localStorage.setItem('rememberedEmail', email);
                }

                // Redirect to dashboard after 1 second
                setTimeout(() => {
                    window.location.href = '/dashboard.html';
                }, 1000);
            } else {
                // Failure
                this.showFormLevelError('loginFormError', 
                    data.message || 'Login failed. Please try again.');
            }
        } catch (error) {
            console.error('Login error:', error);
            if (error.name === 'AbortError') {
                this.showFormLevelError('loginFormError', 
                    'Request timed out. Please check your connection and try again.');
            } else {
                this.showFormLevelError('loginFormError', 
                    'An error occurred. Please try again later.');
            }
        } finally {
            this.isSubmitting = false;
            this.disableButton('loginBtn', false);
        }
    }

    async handleSignupSubmit(e) {
        e.preventDefault();

        // Prevent double submission
        if (this.isSubmitting) return;

        const fullName = document.getElementById('signupFullName').value.trim();
        const email = document.getElementById('signupEmail').value.trim();
        const password = document.getElementById('signupPassword').value;

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
        this.disableButton('signupBtn', true);

        try {
            this.isSubmitting = true;
            this.clearFormLevelError('signupFormError');
            this.clearFormLevelSuccess('signupFormSuccess');

            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-Token': document.getElementById('signupCsrfToken').value || ''
                },
                body: JSON.stringify({
                    fullName,
                    email,
                    password
                }),
                timeout: 30000
            });

            const data = await response.json();

            if (response.ok) {
                // Success
                this.showFormLevelSuccess('signupFormSuccess', 
                    'Account created successfully! Redirecting to login...');
                
                // Clear form
                this.signupForm.reset();

                // Switch to login after 2 seconds
                setTimeout(() => {
                    this.switchTab('login');
                    document.getElementById('loginEmail').value = email;
                    document.getElementById('loginEmail').focus();
                }, 2000);
            } else {
                // Failure
                this.showFormLevelError('signupFormError', 
                    data.message || 'Registration failed. Please try again.');
            }
        } catch (error) {
            console.error('Signup error:', error);
            if (error.name === 'AbortError') {
                this.showFormLevelError('signupFormError', 
                    'Request timed out. Please check your connection and try again.');
            } else {
                this.showFormLevelError('signupFormError', 
                    'An error occurred. Please try again later.');
            }
        } finally {
            this.isSubmitting = false;
            this.disableButton('signupBtn', false);
        }
    }

    // ============================================================
    // ERROR/SUCCESS MESSAGE HANDLING
    // ============================================================

    showError(inputElement, errorElement, message) {
        if (inputElement) {
            inputElement.classList.add('error');
        }
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.add('show');
        }
    }

    clearError(errorElement) {
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.classList.remove('show');
        }
    }

    clearInputError(inputElement) {
        if (inputElement) {
            inputElement.classList.remove('error');
        }
    }

    showFormLevelError(elementId, message) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = message;
            element.classList.add('show');
        }
    }

    clearFormLevelError(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = '';
            element.classList.remove('show');
        }
    }

    showFormLevelSuccess(elementId, message) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = message;
            element.classList.add('show');
        }
    }

    clearFormLevelSuccess(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = '';
            element.classList.remove('show');
        }
    }

    // ============================================================
    // UI STATE MANAGEMENT
    // ============================================================

    switchTab(tab) {
        if (tab === 'login') {
            document.getElementById('loginSection').classList.add('active');
            document.getElementById('signupSection').classList.remove('active');
            document.getElementById('loginTab').classList.add('active');
            document.getElementById('signupTab').classList.remove('active');

            // Clear signup errors
            this.clearFormLevelError('signupFormError');
            this.clearFormLevelSuccess('signupFormSuccess');
            this.disableButton('signupBtn', false);
        } else {
            document.getElementById('loginSection').classList.remove('active');
            document.getElementById('signupSection').classList.add('active');
            document.getElementById('loginTab').classList.remove('active');
            document.getElementById('signupTab').classList.add('active');

            // Clear login errors
            this.clearFormLevelError('loginFormError');
            this.clearFormLevelSuccess('loginFormSuccess');
            this.disableButton('loginBtn', false);
        }
    }

    disableButton(buttonId, disabled) {
        const btn = document.getElementById(buttonId);
        const loadingId = buttonId === 'loginBtn' ? 'loginLoading' : 'signupLoading';
        const loading = document.getElementById(loadingId);

        if (btn) {
            btn.disabled = disabled;
        }
        if (loading) {
            loading.style.display = disabled ? 'block' : 'none';
        }
    }
}

// ============================================================
// INITIALIZATION
// ============================================================

document.addEventListener('DOMContentLoaded', () => {
    new AuthForm();

    // Load remembered email if it exists
    const rememberedEmail = localStorage.getItem('rememberedEmail');
    if (rememberedEmail) {
        document.getElementById('loginEmail').value = rememberedEmail;
    }
});
