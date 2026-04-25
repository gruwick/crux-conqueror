package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.cruxconqueror.crux_conqueror.dto.LeaderboardRow;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.service.NutritionService;
import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.repository.ForumLikeRepo;
import com.cruxconqueror.crux_conqueror.repository.ForumPostRepo;

@Controller
public class HomeController {

    private final UserRepo userRepo;
    private final TrainingSessionsRepo sessionsRepo;
    private final NutritionService nutritionService;
    private final ForumLikeRepo forumLikeRepo;
    private final ForumPostRepo forumPostRepo;

    public HomeController(UserRepo userRepo, TrainingSessionsRepo sessionsRepo, FoodEntryRepo foodEntryRepo,
            NutritionService nutritionService, ForumLikeRepo forumLikeRepo, ForumPostRepo forumPostRepo) {
        this.userRepo = userRepo;
        this.sessionsRepo = sessionsRepo;
        this.nutritionService = nutritionService;
        this.forumLikeRepo = forumLikeRepo;
        this.forumPostRepo = forumPostRepo;
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
        int sessionProgressPercent = Math
                .min((int) Math.round((sessionsLast7Days / (double) weeklySessionTarget) * 100), 100);

        String weeklyTrainingStatus;
        if (sessionsLast7Days >= weeklySessionTarget) {
            weeklyTrainingStatus = "You have hit your weekly training target.";
        } else if (sessionsLast7Days == 2) {
            weeklyTrainingStatus = "You are close to your weekly training target.";
        } else if (sessionsLast7Days == 1) {
            weeklyTrainingStatus = "You have started the week, but more training is needed.";
        } else {
            weeklyTrainingStatus = "No training sessions have been logged in the last 7 days.";
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
        List<ForumPost> topPosts = new ArrayList<>(forumPostRepo.findAllByOrderByCreatedAtDesc());
        Map<Long, Long> topPostLikes = new HashMap<>();
        for (ForumPost post : topPosts) {
            topPostLikes.put(post.getId(), forumLikeRepo.countByPost(post));
        }
        topPosts.sort((a, b) -> Long.compare(
                topPostLikes.getOrDefault(b.getId(), 0L),
                topPostLikes.getOrDefault(a.getId(), 0L)));
        if (topPosts.size() > 3) {
            topPosts = topPosts.subList(0, 3);
        }

        LocalDateTime last30 = LocalDateTime.now().minusDays(30);
        List<TrainingSessions> recent = sessionsRepo.findByArchivedFalseAndSessionDateAfter(last30);
        Map<String, Integer> sessionsPerUser = new HashMap<>();
        for (TrainingSessions s : recent) {
            if (s.getUser() != null && s.getUser().getUsername() != null) {
                String username = s.getUser().getUsername();
                sessionsPerUser.put(username, sessionsPerUser.getOrDefault(username, 0) + 1);
            }
        }
        List<LeaderboardRow> leaderboardPreview = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : sessionsPerUser.entrySet()) {
            leaderboardPreview.add(new LeaderboardRow(
                    entry.getKey(),
                    entry.getValue(), 0, 0.0, "-", 0));
        }
        leaderboardPreview.sort((a, b) -> Integer.compare(
                b.getSessionsLast30(),
                a.getSessionsLast30()));

        int homeRank = -1;
        for (int i = 0; i < leaderboardPreview.size(); i++) {
            if (leaderboardPreview.get(i).getUsername().equals(user.getUsername())) {
                homeRank = i + 1;
                break;
            }
        }
        if (leaderboardPreview.size() > 3) {
            leaderboardPreview = leaderboardPreview.subList(0, 3);
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

        model.addAttribute("topPosts", topPosts);
        model.addAttribute("topPostLikes", topPostLikes);

        model.addAttribute("leaderboardPreview", leaderboardPreview);
        model.addAttribute("homeRank", homeRank);

        return "home/home";
    }
}