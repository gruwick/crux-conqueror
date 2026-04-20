package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;


@Controller
public class DashboardController {
    private final TrainingSessionsRepo sessionsRepo;
    private final UserRepo userRepo;
    private final FoodEntryRepo foodEntryRepo;

    public DashboardController(TrainingSessionsRepo sessionsRepo, UserRepo userRepo, FoodEntryRepo foodEntryRepo){
        this.sessionsRepo = sessionsRepo;
        this.userRepo = userRepo;
        this.foodEntryRepo = foodEntryRepo;
    }

    @GetMapping("/dashboard")

    public String dashboard(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        List<TrainingSessions> sessions =
                sessionsRepo.findByUserAndArchivedFalseOrderBySessionDateDesc(user);
        List<FoodEntry> foodEntries = foodEntryRepo.findByUserOrderByEntryDateTimeDesc(user);

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

                LocalDateTime last14 = now.minusDays(14);
                int sessionsPrevious7Days = (int) sessions.stream()
                .filter(s -> s.getSessionDate() != null
                        && s.getSessionDate().isAfter(last14)
                        && s.getSessionDate().isBefore(last7))
                .count();

                DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
                List<String> chartLabels = new ArrayList<>();
                List<Integer> sessionsChartData = new ArrayList<>();
                List<Integer> hoursChartData = new ArrayList<>();
                List<Double> intensityChartData = new ArrayList<>();
                List<Integer> attemptsChartData = new ArrayList<>();
                List<Integer> topsChartData = new ArrayList<>();
                List<Integer> flashesChartData = new ArrayList<>();

                List<Integer> caloriesChartData = new ArrayList<>();
                List<Integer> proteinChartData = new ArrayList<>();
                List<Integer> fatChartData = new ArrayList<>();
                List<Integer> sugarChartData = new ArrayList<>();
                List<Integer> saltChartData = new ArrayList<>();

                for (int i = 6; i >= 0; i--) {
                LocalDate day = LocalDate.now().minusDays(i);
                chartLabels.add(day.format(dayFormatter));

                List<TrainingSessions> daySessions = sessions.stream()
                        .filter(s -> s.getSessionDate() != null)
                        .filter(s -> s.getSessionDate().toLocalDate().equals(day))
                        .toList();

                sessionsChartData.add(daySessions.size());

                hoursChartData.add(
                        (int) Math.round(
                         daySessions.stream()
                        .filter(s -> s.getDurationMinutes() != null)
                        .mapToInt(TrainingSessions::getDurationMinutes)
                        .sum() / 60.0
                        )
                );

                intensityChartData.add(daySessions.stream()
                        .filter(s -> s.getIntensity() != null)
                        .mapToInt(TrainingSessions::getIntensity)
                        .average()
                        .orElse(0.0));

                attemptsChartData.add(daySessions.stream()
                        .mapToInt(s -> s.getAttemptsTotal() == null ? 0 : s.getAttemptsTotal())
                        .sum());

                topsChartData.add(daySessions.stream()
                        .mapToInt(s -> s.getTopsTotal() == null ? 0 : s.getTopsTotal())
                        .sum());

                flashesChartData.add(daySessions.stream()
                        .mapToInt(s -> s.getFlashesTotal() == null ? 0 : s.getFlashesTotal())
                        .sum());
                
                List<FoodEntry> dayFoodEntries = foodEntries.stream()
                        .filter(e -> e.getEntryDateTime() != null)
                        .filter(e -> e.getEntryDateTime().toLocalDate().equals(day))
                        .toList();

                caloriesChartData.add(dayFoodEntries.stream()
                        .mapToInt(e -> e.getCalories() == null ? 0 : e.getCalories())
                        .sum());

                proteinChartData.add(dayFoodEntries.stream()
                        .mapToInt(e -> e.getProtein() == null ? 0 : e.getProtein())
                        .sum());

                fatChartData.add(dayFoodEntries.stream()
                        .mapToInt(e -> e.getFats() == null ? 0 : e.getFats())
                        .sum());

                sugarChartData.add(dayFoodEntries.stream()
                        .mapToInt(e -> e.getSugar() == null ? 0 : e.getSugar())
                        .sum());

                saltChartData.add(dayFoodEntries.stream()
                        .mapToInt(e -> e.getSalt() == null ? 0 : e.getSalt())
                        .sum());
                }


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

        model.addAttribute("sessionsPrevious7Days", sessionsPrevious7Days);
        model.addAttribute("chartLabels", chartLabels);

        model.addAttribute("chartData", sessionsChartData);
        model.addAttribute("hoursChartData", hoursChartData);
        model.addAttribute("intensityChartData", intensityChartData);
        model.addAttribute("attemptsChartData", attemptsChartData);
        model.addAttribute("topsChartData", topsChartData);
        model.addAttribute("flashesChartData", flashesChartData);

        model.addAttribute("caloriesChartData", caloriesChartData);
        model.addAttribute("proteinChartData", proteinChartData);
        model.addAttribute("fatChartData", fatChartData);
        model.addAttribute("sugarChartData", sugarChartData);
        model.addAttribute("saltChartData", saltChartData);


        return "dashboard/dashboard";
    }
}

