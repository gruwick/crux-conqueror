package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (principal == null) {
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

        LocalDateTime last7 = LocalDateTime.now().minusDays(7);
        int sessionsLast7Days = (int) sessionsRepo.findByUserAndArchivedFalseOrderBySessionDateDesc(user).stream()
                .filter(s -> s.getSessionDate() != null && s.getSessionDate().isAfter(last7))
                .count();

        int weeklySessionTarget = 3;
        int sessionProgressPercent = Math.min((int) Math.round((sessionsLast7Days / (double) weeklySessionTarget) * 100), 100);

        String weeklyTrainingStatus;
        if (sessionsLast7Days >= weeklySessionTarget) {
            weeklyTrainingStatus = "You have hit your weekly training target.";
        } else if (sessionsLast7Days == 2) {weeklyTrainingStatus = "You are close to your weekly training target.";
        } else if (sessionsLast7Days == 1) {weeklyTrainingStatus = "You have started the week, but more training is needed.";
        } else {weeklyTrainingStatus = "No training sessions have been logged in the last 7 days.";
        }

        List<String> suggestions = new ArrayList<>();

        if (sessionsLast7Days < weeklySessionTarget) {
            suggestions.add("You are behind on your weekly training target.");
        }

        if (caloriesToday == 0) {
            suggestions.add("No nutrition has been logged today.");
        }

        if (proteinToday > 0 && user.getProteinGoal() != null && proteinToday < user.getProteinGoal()) {
            suggestions.add("Protein intake is still below your daily target.");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("You are progressing well this week. Keep building consistency.");
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("caloriesToday", caloriesToday);
        model.addAttribute("proteinToday", proteinToday);
        model.addAttribute("latestMeal", latestMeal);
        model.addAttribute("latestSessionGrade", latestSessionGrade);

        model.addAttribute("sessionsLast7Days", sessionsLast7Days);
        model.addAttribute("weeklySessionTarget", weeklySessionTarget);
        model.addAttribute("sessionProgressPercent", sessionProgressPercent);
        model.addAttribute("weeklyTrainingStatus", weeklyTrainingStatus);
        model.addAttribute("suggestions", suggestions);

        return "home/home";
    }
}