package com.cruxconqueror.crux_conqueror.controller;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.service.NutritionService;

@Controller
public class HomeController {

    private final UserRepo userRepo;
    private final TrainingSessionsRepo sessionsRepo;
    private final NutritionService nutritionService;

    public HomeController(UserRepo userRepo, TrainingSessionsRepo sessionsRepo, FoodEntryRepo foodEntryRepo, NutritionService nutritionService){
        this.userRepo = userRepo;
        this.sessionsRepo = sessionsRepo;
        this.nutritionService = nutritionService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        if(principal == null) {
        return "home/home";
    }
          User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

        List<FoodEntry> todaysEntries = nutritionService.getTodaysEntries(user);
        int caloriesToday = nutritionService.getCaloriesFromEntries(todaysEntries);
        int proteinToday = nutritionService.getProteinFromEntries(todaysEntries);
        String latestMeal = nutritionService.getLatestMeal(user);

        String latestSessionGrade = sessionsRepo.findFirstByUserAndArchivedFalseOrderBySessionDateDesc(user)
                .map(s -> s.getHighestGrade() == null || s.getHighestGrade().isBlank() ? "N/A" : s.getHighestGrade())
                .orElse("No sessions logged");

        long totalSessions = sessionsRepo.countByUserAndArchivedFalse(user);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("caloriesToday", caloriesToday);
        model.addAttribute("proteinToday", proteinToday);
        model.addAttribute("latestMeal", latestMeal);
        model.addAttribute("latestSessionGrade", latestSessionGrade);

        return "home/home";  
}
}
