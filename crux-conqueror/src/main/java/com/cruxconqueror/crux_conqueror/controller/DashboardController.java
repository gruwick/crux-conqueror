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
        
        String climbingSummary;

        if (attemptsLast30Days == 0) {climbingSummary = "No bouldering attempts were logged in the last 30 days.";} 
        else if (topsLast30Days == 0) {climbingSummary = "Attempts were logged, but no tops were recorded in the last 30 days.";} 
        else {double topRate = (double) topsLast30Days / attemptsLast30Days;
        if (topRate < 0.3) {climbingSummary = "A high number of attempts resulted in relatively few tops, suggesting difficulty completing climbs consistently.";} 
        else if (topRate < 0.6) {climbingSummary = "Bouldering performance appears balanced, with a moderate proportion of attempts resulting in tops.";} 
        else {climbingSummary = "A strong proportion of attempts resulted in tops, indicating effective route completion.";}
        }

        List<TrainingSessions> recentSessions = sessions.stream().limit(5).toList();

                LocalDateTime last14 = now.minusDays(14);
                int sessionsPrevious7Days = (int) sessions.stream()
                .filter(s -> s.getSessionDate() != null
                        && s.getSessionDate().isAfter(last14)
                        && s.getSessionDate().isBefore(last7))
                .count();

                String trainingSummary;

                if (sessionsLast7Days == 0) {
                trainingSummary = "No training sessions were logged in the last 7 days.";
                } 
                else if (sessionsLast7Days > sessionsPrevious7Days) {
                trainingSummary = "Training frequency has increased compared with the previous 7 days.";
                } 
                else if (sessionsLast7Days < sessionsPrevious7Days) {
                trainingSummary = "Training frequency has decreased compared with the previous 7 days.";
                } 
                else {
                trainingSummary = "Training frequency matches the previous 7 days.";
                }

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

                int dayCals = dayFoodEntries.stream()
                .mapToInt(e ->e.getCalories() == null ? 0: e.getCalories())
                .sum();

                int dayProtein = dayFoodEntries.stream()
                .mapToInt(e ->e.getProtein() == null ? 0: e.getProtein())
                .sum();

                int dayFat = dayFoodEntries.stream()
                .mapToInt(e ->e.getFats() == null ? 0: e.getFats())
                .sum();

                caloriesChartData.add(user.getCalorieGoal() != null && user.getCalorieGoal() > 0
                        ? (int) Math.round(((double) dayCals / user.getCalorieGoal()) *100) : 0);
                
                proteinChartData.add(user.getProteinGoal() != null && user.getProteinGoal() > 0
                        ? (int) Math.round(((double) dayProtein / user.getProteinGoal()) *100) : 0);

                fatChartData.add(user.getFatGoal() != null && user.getFatGoal() > 0
                        ? (int) Math.round(((double) dayFat / user.getFatGoal()) *100) : 0);


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



        int latestCaloriesPercent = 0;
        int latestProteinPercent = 0;
        int latestFatPercent = 0;

        for (int i = caloriesChartData.size() - 1; i >= 0; i--) {
        if (caloriesChartData.get(i) > 0 || proteinChartData.get(i) > 0 || fatChartData.get(i) > 0) {
                latestCaloriesPercent = caloriesChartData.get(i);
                latestProteinPercent = proteinChartData.get(i);
                latestFatPercent = fatChartData.get(i);
                break;
                }
        }

        String calorieStatus;
        String proteinStatus;
        String fatStatus;

        if (latestCaloriesPercent < 90) {
        calorieStatus = "below";
        } else if (latestCaloriesPercent <= 110) {
        calorieStatus = "near";
        } else {
        calorieStatus = "above";
        }

        if (latestProteinPercent < 90) {
        proteinStatus = "below";
        } else if (latestProteinPercent <= 110) {
        proteinStatus = "near";
        } else {
        proteinStatus = "above";
        }

        if (latestFatPercent < 90) {
        fatStatus = "below";
        } else if (latestFatPercent <= 110) {
        fatStatus = "near";
        } else {
        fatStatus = "above";
        }

        String nutritionSummary;
        if (latestCaloriesPercent == 0 && latestProteinPercent == 0 && latestFatPercent == 0) {
        nutritionSummary = "No nutrition has been logged recently.";
        } 
        else if ("below".equals(calorieStatus) && "above".equals(proteinStatus)) {
        nutritionSummary = "Recent intake is below calorie target, while protein intake is above target.";
        } 
        else if ("above".equals(calorieStatus) && "above".equals(fatStatus)) {
        nutritionSummary = "Recent intake is above calorie target, with fat intake also above target.";
        } 
        else if ("near".equals(calorieStatus) && "near".equals(proteinStatus) && "near".equals(fatStatus)) {
        nutritionSummary = "Recent intake is close to target across calories, protein, and fat.";
        } 
        else if ("below".equals(calorieStatus) && "below".equals(proteinStatus)) {
        nutritionSummary = "Recent intake is below target for both calories and protein.";
        } 
        else if ("above".equals(proteinStatus) && "below".equals(fatStatus)) {
        nutritionSummary = "Protein intake is above target, while fat intake remains below target.";
        } 
        else {
        nutritionSummary = "Recent intake shows mixed progress across calories and macronutrients.";
        }
        model.addAttribute("latestCaloriesPercent", latestCaloriesPercent);
        model.addAttribute("latestProteinPercent", latestProteinPercent);
        model.addAttribute("latestFatPercent", latestFatPercent);
        model.addAttribute("nutritionSummary", nutritionSummary);
        
        model.addAttribute("calorieGoal", user.getCalorieGoal());
        model.addAttribute("proteinGoal", user.getProteinGoal());
        model.addAttribute("fatGoal", user.getFatGoal());
        model.addAttribute("goalType", user.getGoalType());
        model.addAttribute("activityLevel", user.getActivityLevel());
        return "dashboard/dashboard";
    }

}

