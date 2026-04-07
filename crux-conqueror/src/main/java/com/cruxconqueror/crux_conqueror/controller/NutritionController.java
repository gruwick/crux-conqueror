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
import com.cruxconqueror.crux_conqueror.service.NutritionService;

@Controller
@RequestMapping("/nutrition")
public class NutritionController {
    private final FoodEntryRepo foodEntryRepo;
    private final UserRepo userRepo;
    private final NutritionService nutritionService;

    public NutritionController(FoodEntryRepo foodEntryRepo, UserRepo userRepo, NutritionService nutritionService){
        this.foodEntryRepo = foodEntryRepo;
        this.userRepo = userRepo;
        this.nutritionService = nutritionService;
    }
    
    @GetMapping
    public String list(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));

        List<FoodEntry> todaysEntries = nutritionService.getTodaysEntries(user);
        int calories = nutritionService.getCaloriesFromEntries(todaysEntries);
        int protein = nutritionService.getProteinFromEntries(todaysEntries);
        int carbs = nutritionService.getCarbsFromEntries(todaysEntries);
        int fat = nutritionService.getFatsFromEntries(todaysEntries);
        int sugar = nutritionService.getSugarFromEntries(todaysEntries);
        int salt = nutritionService.getSaltFromEntries(todaysEntries);


        List<FoodEntry> recentEntries = todaysEntries.stream().limit(10).toList();
        model.addAttribute("entries", recentEntries);
        model.addAttribute("recentEntries", recentEntries);
        model.addAttribute("totalCalories", calories);
        model.addAttribute("totalCarbs", carbs);
        model.addAttribute("totalProtein", protein);
        model.addAttribute("totalFats", fat);
        model.addAttribute("totalSugar", sugar);
        model.addAttribute("totalSalt", salt);

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
        @PostMapping
    public String create(@ModelAttribute("entry") FoodEntry entry, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        if (entry.getMealType() == null || entry.getMealType().isBlank()) {
            throw new IllegalArgumentException("Meal type is required");
        }
        if (entry.getFoodName() == null || entry.getFoodName().isBlank()) {
            throw new IllegalArgumentException("Food name is required");
        }
        if (entry.getEntryDateTime() == null) {
            entry.setEntryDateTime(LocalDateTime.now());
        }

        if (entry.getCalories() == null) entry.setCalories(0);
        if (entry.getCarbs() == null) entry.setCarbs(0);
        if (entry.getProtein() == null) entry.setProtein(0);
        if (entry.getFats() == null) entry.setFats(0);
        if (entry.getSugar() == null) entry.setSugar(0);
        if (entry.getSalt() == null) entry.setSalt(0);

        entry.setUser(user);

        foodEntryRepo.save(entry);

        return "redirect:/nutrition";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        FoodEntry entry = requireOwnedEntry(id, principal);

        model.addAttribute("entry", entry);
        model.addAttribute("formTitle", "Edit food entry");
        model.addAttribute("formAction", "/nutrition/" + id + "/edit");
        model.addAttribute("submitText", "Update entry");

        return "nutrition/new";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("entry") FoodEntry updated,
                             Principal principal) {

        FoodEntry existing = requireOwnedEntry(id, principal);

        if (updated.getMealType() == null || updated.getMealType().isBlank()) {
            throw new IllegalArgumentException("Meal type is required");
        }
        if (updated.getFoodName() == null || updated.getFoodName().isBlank()) {
            throw new IllegalArgumentException("Food name is required");
        }
        if (updated.getEntryDateTime() == null) {
            updated.setEntryDateTime(existing.getEntryDateTime());
        }

        existing.setEntryDateTime(updated.getEntryDateTime());
        existing.setMealType(updated.getMealType());
        existing.setFoodName(updated.getFoodName());

        existing.setCalories(updated.getCalories() == null ? 0 : updated.getCalories());
        existing.setCarbs(updated.getCarbs() == null ? 0 : updated.getCarbs());
        existing.setProtein(updated.getProtein() == null ? 0 : updated.getProtein());
        existing.setFats(updated.getFats() == null ? 0 : updated.getFats());
        existing.setSugar(updated.getSugar() == null ? 0 : updated.getSugar());
        existing.setSalt(updated.getSalt() == null ? 0 : updated.getSalt());

        existing.setAdditionalThoughts(updated.getAdditionalThoughts());

        foodEntryRepo.save(existing);

        return "redirect:/nutrition";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        FoodEntry entry = requireOwnedEntry(id, principal);
        foodEntryRepo.delete(entry);
        return "redirect:/nutrition";
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
    

