package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cruxconqueror.crux_conqueror.dto.LeaderboardRow;
import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;

@Controller
public class LeaderboardController {

    private final TrainingSessionsRepo sessionsRepo;

    public LeaderboardController(TrainingSessionsRepo sessionsRepo) {
        this.sessionsRepo = sessionsRepo;
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model, Principal principal,
        @RequestParam(defaultValue = "global") String scope,
        @RequestParam(defaultValue = "sessions") String metric
    ) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last30 = now.minusDays(30);

        // Pull recent active sessions for all users
        List<TrainingSessions> recent = sessionsRepo.findByArchivedFalseAndSessionDateAfter(last30);

        // Group by username
        Map<String, List<TrainingSessions>> byUser = recent.stream()
                .filter(s -> s.getUser() != null && s.getUser().getUsername() != null)
                .collect(Collectors.groupingBy(s -> s.getUser().getUsername()));

        List<LeaderboardRow> rows = new ArrayList<>();

        for (Map.Entry<String, List<TrainingSessions>> e : byUser.entrySet()) {
            String username = e.getKey();
            List<TrainingSessions> sessions = e.getValue();

            int count = sessions.size();
            int minutes = sessions.stream()
                    .map(TrainingSessions::getDurationMinutes)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();

            double avgIntensity = sessions.stream()
                    .map(TrainingSessions::getIntensity)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            
            String bestGrade = sessions.stream()
                    .map(TrainingSessions::getHighestGrade)
                    .filter(g -> g != null && !g.isBlank())
                    .max(Comparator.comparingInt(this::gradeToScore))
                    .orElse("-");

            int bestGradeScore = gradeToScore(bestGrade);

            rows.add(new LeaderboardRow(username, count, minutes, avgIntensity, bestGrade, bestGradeScore));
        }

        // Sort: sessions desc, minutes desc, username asc
            rows.sort((a, b) -> {

                if ("minutes".equals(metric)) {
                    return Integer.compare(b.getMinutesLast30(), a.getMinutesLast30());
                }

                if ("intensity".equals(metric)) {
                    return Double.compare(b.getAvgIntensityLast30(), a.getAvgIntensityLast30());
                }

                if ("grade".equals(metric)) {
                    return Integer.compare(b.getBestGradeScore(), a.getBestGradeScore());
                }

                return Integer.compare(b.getSessionsLast30(), a.getSessionsLast30());
            });

                //Error testing
            System.out.println("---- SORTED ROWS ----");
            rows.forEach(r -> System.out.println(r.getUsername()
                + " sessions=" + r.getSessionsLast30()
                + " minutes=" + r.getMinutesLast30()));
                    System.out.println("---------------------");

        // Find current user's rank
        String me = principal != null ? principal.getName() : null;
        int myIndex = -1;
        if (me != null) {
            for (int i = 0; i < rows.size(); i++) {
                if (me.equals(rows.get(i).getUsername())) {
                    myIndex = i;
                    break;
                }
            }
        }

        model.addAttribute("rows", rows);
        model.addAttribute("myIndex", myIndex);
        model.addAttribute("scope", scope);
        model.addAttribute("metric, metric");

        return "community/leaderboard";
    }
    private int gradeToScore(String grade){
        if (grade == null || grade.isBlank()) return 0;
    try{
        return Integer.parseInt(grade.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
        return 0;
    }
    }
}