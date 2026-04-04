package com.cruxconqueror.crux_conqueror.controller;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;

@Controller
public class HomeController {

    private final UserRepo userRepo;
    private final TrainingSessionsRepo sessionsRepo;
    private final FoodEntryRepo foodEntryRepo;

    public HomeController(UserRepo userRepo, TrainingSessionsRepo sessionsRepo, FoodEntryRepo foodEntryRepo){
        this.userRepo = userRepo;
        this.sessionsRepo = sessionsRepo;
        this.foodEntryRepo = foodEntryRepo;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        if(principal == null) {
        return "home";
    }
          User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<FoodEntry> todaysEntries =
                foodEntryRepo.findByUserAndEntryDateTimeBetweenOrderByEntryDateTimeDesc(
                        user, startOfDay, endOfDay);

        int caloriesToday = todaysEntries.stream()
                .map(FoodEntry::getCalories)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        int proteinToday = todaysEntries.stream()
                .map(FoodEntry::getProtein)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        String latestMeal = foodEntryRepo.findFirstByUserOrderByEntryDateTimeDesc(user)
                .map(FoodEntry::getFoodName)
                .orElse("No meals logged");

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

        return "home";  
}
}
