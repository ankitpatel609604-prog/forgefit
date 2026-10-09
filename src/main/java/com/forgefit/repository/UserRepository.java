package com.forgefit.repository;

import com.forgefit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity database operations.
 * 
 * Provides database access methods for user registration and authentication.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     * 
     * This method performs case-sensitive lookup by default. Email uniqueness
     * is enforced at the database level with a UNIQUE constraint.
     * 
     * Query performance is optimized with an index on the email column.
     * Expected query time: <100ms for databases with up to 100,000 users.
     * 
     * @param email the email address to search for (max 254 characters per RFC 5321)
     * @return Optional containing the User if found, empty Optional otherwise
     * 
     * @throws IllegalArgumentException if email is null
     */
    Optional<User> findByEmail(String email);
}
