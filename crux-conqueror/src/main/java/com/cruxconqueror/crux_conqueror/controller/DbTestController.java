package com.cruxconqueror.crux_conqueror.controller;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.model.User;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/test")
public class DbTestController {
private final UserRepo userRepo;

public DbTestController(UserRepo userRepo){
    this.userRepo = userRepo;
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
    //Reminder to myself to remove this after testing, not needed long term
    User u = new User(username, email, password);
    return userRepo.save(u);
}
}