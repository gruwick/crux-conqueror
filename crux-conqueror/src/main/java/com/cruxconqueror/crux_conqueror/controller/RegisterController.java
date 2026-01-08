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

@Controller
public class RegisterController {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid @ModelAttribute("form") RegisterForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        if (userRepo.existsByUsername(form.getUsername())) {
            model.addAttribute("usernameTaken", true);
            return "auth/register";
        }

        if (userRepo.existsByEmail(form.getEmail())) {
            model.addAttribute("emailTaken", true);
            return "auth/register";
        }

        String hashed = passwordEncoder.encode(form.getPassword());

        User u = new User(form.getUsername(), form.getEmail(), hashed);
        userRepo.save(u);

        return "redirect:/login?registered";
    }
}
