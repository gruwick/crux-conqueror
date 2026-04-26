package com.cruxconqueror.crux_conqueror.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * DTO for handling user registration form input
 * Uses validation annotation to enfore input rules before the data reaches the controller
 */
public class RegisterForm {
    //Username must be between 3 and 25 characters
    @NotBlank
    @Size(min = 3, max = 25)
    private String username;
    //Email must be in valid format and not exceed 50 characters
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    //Passwords must be between 6 and 72 characters
    @NotBlank
    @Size(min = 6, max = 72)

    private String password;
    //Getters and setters for form binding
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
