package com.cruxconqueror.crux_conqueror.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Main Security Config for the application
 * 
 * Sets up the folowwing:
 * -Password hashing (BCrypt)
 * -Authentication using custom UserDetailsService
 * -Route protection for login
 * -login and out behaviours
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    //BCrypt used to securely has user passwords before storing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //Connects Spring Security to our custom UserDetailService
    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Uses DbUserDetailsService to load users from database
        provider.setUserDetailsService(userDetailsService);

        // Ensures passwords are checked using BCrypt hashing
        provider.setPasswordEncoder(passwordEncoder);
        return provider;

    }
    // Defines the routes that are secured and how login and logout behaves
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //CSRF disables for simplcity during dev, can be reenabled in production
                .csrf(csrf -> csrf.disable())

                //Definine public vs private routes
                .authorizeHttpRequests(auth -> auth
                        // allows acces to static resources without login
                        .requestMatchers("/css/**", "/images/**", "/js/**, /test/**").permitAll()

                        //allow access to login/register pages
                        .requestMatchers("/login", "/register", "/error").permitAll()

                        //All other pages require authentication
                        .anyRequest().authenticated())
                
                //Custom login cnfig
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())

                //logout behaviour
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout"));

        return http.build();
    }

}
