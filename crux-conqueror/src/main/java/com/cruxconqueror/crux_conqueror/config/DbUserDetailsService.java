package com.cruxconqueror.crux_conqueror.config;

import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import java.util.Collections;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {
    
    private final UserRepo userRepo;

    public DbUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                return new org.springframework.security.core.userdetails.User(
                    u.getUsername(),
                    u.getPasswordHash(),
                    Collections.emptyList()
                );
    }
}
