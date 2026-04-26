package com.cruxconqueror.crux_conqueror.controller;

import com.cruxconqueror.crux_conqueror.dto.RegisterForm;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
/** Controller is responsible for user registration
 * 
 * Handles:
 * displaying the registration form
 * validating inputs 
 * Creating new user accounts
 */
@Controller
public class RegisterController {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    //Displays the register page
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }
    /** Handles form submission for user registration
     * Performs:
     * Validation checks
     * Duplicate username/email checks
     * Password hashing
     */
    @PostMapping("/register")
    public String doRegister(
            @Valid @ModelAttribute("form") RegisterForm form,
            BindingResult bindingResult,
            Model model) {
        //If validation fail, return to form
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        //check if username is already taken
        if (userRepo.existsByUsername(form.getUsername())) {
            model.addAttribute("usernameTaken", true);
            return "auth/register";
        }
        //check if email is already taken
        if (userRepo.existsByEmail(form.getEmail())) {
            model.addAttribute("emailTaken", true);
            return "auth/register";
        }
        //Hashpasswords using BCrypt before storing in database
        String hashed = passwordEncoder.encode(form.getPassword());
        //Create and save new user
        User u = new User(form.getUsername(), form.getEmail(), hashed);
        userRepo.save(u);
        //return to login after registration
        return "redirect:/login?registered";
    }
}
