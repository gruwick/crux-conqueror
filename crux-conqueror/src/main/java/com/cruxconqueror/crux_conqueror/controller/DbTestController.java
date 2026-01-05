package com.cruxconqueror.crux_conqueror.controller;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.model.User;
import java.util.List;
import org.springframework.web.bind.annotation.*;

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
}