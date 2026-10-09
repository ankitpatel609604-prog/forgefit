package com.forgefit;

import com.forgefit.model.User;
import com.forgefit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader creates initial test data on application startup.
 * 
 * For the login/authentication feature, this creates a test user so you can verify
 * the authentication flow works correctly.
 * 
 * Test credentials:
 * - Email: test@forgefit.com
 * - Password: TestPass123!
 * 
 * Note: Since the database is in-memory (H2), all data is cleared on restart.
 * This loader ensures a fresh test user is available after each startup.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create test user if not already present
        if (userRepository.findByEmail("test@forgefit.com").isEmpty()) {
            User testUser = new User();
            testUser.setFullName("Test User");
            testUser.setEmail("test@forgefit.com");
            testUser.setPassword(passwordEncoder.encode("TestPass123!"));
            testUser.setRole("USER");
            
            userRepository.save(testUser);
            System.out.println("✓ Test user created: test@forgefit.com / TestPass123!");
        }
    }
}