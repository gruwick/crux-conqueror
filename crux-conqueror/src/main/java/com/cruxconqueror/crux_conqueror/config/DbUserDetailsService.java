package com.cruxconqueror.crux_conqueror.config;

import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import java.util.Collections;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Implementation of my UserDetails service whcih is used by spring security.
 * Retrieved user data from my database and converts it to a format that sping
 * Security can use for authentication
 * Allows it to be called automatically during login to verify user credentials
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public DbUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Looks up users by username when login is submitted
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        //Return the detials spring Security needs
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPasswordHash(),
                Collections.emptyList());
    }
}
