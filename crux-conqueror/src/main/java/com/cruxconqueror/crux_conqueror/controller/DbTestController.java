package com.cruxconqueror.crux_conqueror.controller;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class DbTestController {

private final UserRepo userRepo;
private final PasswordEncoder passwordEncoder;

public DbTestController(UserRepo userRepo, PasswordEncoder passwordEncoder){
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
}

@GetMapping("/users")
public List<User> users() {
    return userRepo.findAll();
}

@GetMapping("/create")
public User createUser(
    @RequestParam String username,
    @RequestParam String email,
    @RequestParam String password
) {
    if(username.isBlank()||email.isBlank()||password.isBlank() ){
        throw new IllegalArgumentException("Your Username, Email or Password is empty and must be changed");
    }
     if(userRepo.existsByUsername(username)){
        throw new IllegalArgumentException("Your Username already exists and must be changed");
    }
    if(userRepo.existsByEmail(email)){
        throw new IllegalArgumentException("Your Email already exists and must be changed");
    }

    String hashedPassword = passwordEncoder.encode(password);
    //Reminder to myself to remove this after testing, not needed long term
    //Storing the hashed password now and not the raw
    User u = new User(username, email, hashedPassword);
    return userRepo.save(u);
}
}