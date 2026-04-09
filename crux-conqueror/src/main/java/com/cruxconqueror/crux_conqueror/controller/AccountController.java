package com.cruxconqueror.crux_conqueror.controller;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;


@Controller
public class AccountController {
    private final UserRepo userRepo;

    public AccountController(UserRepo userRepo){
        this.userRepo = userRepo;
    }
    @GetMapping("/account")
    public String account(Model model, Principal principal) {
        if(principal == null){
            return "redirect:/login";
        }
        User user = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

        model.addAttribute("user", user);
        return "Account/account";
    }

    @PostMapping("/account")
    public String updateAccount(Principal principal, @RequestParam(required = false) String bio,
                @RequestParam(required = false) Integer age,@RequestParam(required = false) Double heightCm,@RequestParam(required = false) Double weightKg,
                @RequestParam(required = false) String experienceLevel,@RequestParam(required = false) String goalType,@RequestParam(required = false) String activityLevel,
                @RequestParam(required = false) String bioVisibility,@RequestParam(required = false) String ageVisibility,@RequestParam(required = false) String heightVisibility,
                @RequestParam(required = false) String weightVisibility,@RequestParam(required = false) String experienceVisibility
                ) {
                    if(principal ==null){
                        return "redirect:/login";
                    }
                    User user = userRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

                    user.setBio(bio);
                    user.setAge(age);
                    user.setHeightCm(heightCm);
                    user.setWeightKg(weightKg);
                    user.setExperienceLevel(experienceLevel);
                    user.setGoalType(goalType);
                    user.setActivityLevel(activityLevel);
                    user.setBioVisibility(bioVisibility);
                    user.setAgeVisibility(ageVisibility);
                    user.setHeightVisibility(heightVisibility);
                    user.setWeightVisibility(weightVisibility);
                    user.setExperienceVisibility(experienceVisibility);
                    userRepo.save(user);
    
        
        return "redirect:/account";
    }
    
    

    
}
