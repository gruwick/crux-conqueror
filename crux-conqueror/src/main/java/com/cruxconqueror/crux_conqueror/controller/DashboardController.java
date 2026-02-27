package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;


@Controller
public class DashboardController {
    private final TrainingSessionsRepo sessionsRepo;
    private final UserRepo userRepo;

    public DashboardController(TrainingSessionsRepo sessionsRepo, UserRepo userRepo){
        this.sessionsRepo = sessionsRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/dashboard")

    public String dashboard(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        List<TrainingSessions> sessions =
                sessionsRepo.findByUserAndArchivedFalseOrderBySessionDateDesc(user);

       LocalDateTime now = LocalDateTime.now();
       LocalDateTime last7 = now.minusDays(7);
       LocalDateTime last30 = now.minusDays(30);
       int totalsessions = sessions.size();

        int sessionsLast7Days = (int) sessions.stream()
                .filter(s -> s.getSessionDate() != null && s.getSessionDate().isAfter(last7))
                .count();

        List<TrainingSessions> sessionsLast30 = sessions.stream()
                .filter(s -> s.getSessionDate() != null && s.getSessionDate().isAfter(last30))
                .toList();

        int sessionsLast30Days = sessionsLast30.size();

        int totalMinutesLast30Days = sessionsLast30.stream()
                .filter(s -> s.getDurationMinutes() != null)
                .mapToInt(TrainingSessions::getDurationMinutes)
                .sum();

        double avgIntensityLast30Days = sessionsLast30.stream()
                .filter(s -> s.getIntensity() != null)
                .mapToInt(TrainingSessions::getIntensity)
                .average()
                .orElse(0.0);

        int attemptsLast30Days = sessionsLast30.stream()
                .mapToInt(s -> s.getAttemptsTotal() == null ? 0 : s.getAttemptsTotal())
                .sum();

        int topsLast30Days = sessionsLast30.stream()
                .mapToInt(s -> s.getTopsTotal() == null ? 0 : s.getTopsTotal())
                .sum();

        int flashesLast30Days = sessionsLast30.stream()
                .mapToInt(s -> s.getFlashesTotal() == null ? 0 : s.getFlashesTotal())
                .sum();

        List<TrainingSessions> recentSessions = sessions.stream().limit(5).toList();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("totalSessions", totalsessions);
        model.addAttribute("sessionsLast7Days", sessionsLast7Days);
        model.addAttribute("sessionsLast30Days", sessionsLast30Days);

        model.addAttribute("totalMinutesLast30Days", totalMinutesLast30Days);
        model.addAttribute("avgIntensityLast30Days", avgIntensityLast30Days);

        model.addAttribute("attemptsLast30Days", attemptsLast30Days);
        model.addAttribute("topsLast30Days", topsLast30Days);
        model.addAttribute("flashesLast30Days", flashesLast30Days);

        model.addAttribute("recentSessions", recentSessions);

        return "dashboard";
    }
}

