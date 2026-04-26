package com.cruxconqueror.crux_conqueror.repository;

import com.cruxconqueror.crux_conqueror.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
/** Repository for accessing user data
 * 
 * Supports authentication, validation checks
 * and user search functionality
 */
public interface UserRepo extends JpaRepository<User, Long> {
    //return user by username
    //used for authentication
    Optional<User> findByUsername(String username);
    //Searches for partial username match
    //used for searching or adding friends
    List<User> findByUsernameContainingIgnoreCase(String username);
    //Checks if a username exists
    //used during registration validation
    boolean existsByUsername(String username);
    // checks if an email exits
    //used during registration validation
    boolean existsByEmail(String email);
}
