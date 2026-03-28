package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;

@Controller
@RequestMapping("/nutrition")
public class NutritionController {
    private final FoodEntryRepo foodEntryRepo;
    private final UserRepo userRepo;

    public NutritionController(FoodEntryRepo foodEntryRepo, UserRepo userRepo){
        this.foodEntryRepo = foodEntryRepo;
        this.userRepo = userRepo;
    }
    
    @GetMapping
    public String list(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
        List<FoodEntry> entries = foodEntryRepo.findByUserOrderByEntryDateTimeDesc(user);

        int totalCalories = entries.stream()
                .map(FoodEntry::getCalories)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalCarbs = entries.stream()
                .map(FoodEntry::getCarbs)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalProtein = entries.stream()
                .map(FoodEntry::getProtein)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalFats = entries.stream()
                .map(FoodEntry::getFats)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalSugar = entries.stream()
                .map(FoodEntry::getSugar)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalSalt = entries.stream()
                .map(FoodEntry::getSalt)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();

        List<FoodEntry> recentEntries = entries.stream().limit(10).toList();
        model.addAttribute("entries", entries);
        model.addAttribute("recentEntries", recentEntries);
        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("totalCarbs", totalCarbs);
        model.addAttribute("totalProtein", totalProtein);
        model.addAttribute("totalFats", totalFats);
        model.addAttribute("totalSugar", totalSugar);
        model.addAttribute("totalSalt", totalSalt);

        return "nutrition/list";

    }

    @GetMapping("/new")
    public String newEntry(Model model) {
        FoodEntry entry = new FoodEntry();
        entry.setEntryDateTime(LocalDateTime.now());

        model.addAttribute("entry", entry);
        model.addAttribute("formTitle", "Add food entry");
        model.addAttribute("formAction", "/nutrition");
        model.addAttribute("submitText", "Save entry");

        return "nutrition/new";
    }
        private FoodEntry requireOwnedEntry(Long id, Principal principal) {
        FoodEntry entry = foodEntryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food entry not found"));

        if (entry.getUser() == null || entry.getUser().getUsername() == null) {
            return entry;
        }

        if (!entry.getUser().getUsername().equals(principal.getName())) {
            throw new IllegalArgumentException("You do not have access to this food entry");
        }

        return entry;
    }
}
    

