package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.service.NutritionService;

/** Controller for handling nutrition related features
 * 
 * Includes:
 * Viewing daily nutrition data
 * Creating,editing and deleting food entries
 * Displaying weekly navigation
 */
@Controller
@RequestMapping("/nutrition")
public class NutritionController {
    private final FoodEntryRepo foodEntryRepo;
    private final UserRepo userRepo;
    private final NutritionService nutritionService;

    public NutritionController(FoodEntryRepo foodEntryRepo, UserRepo userRepo, NutritionService nutritionService) {
        this.foodEntryRepo = foodEntryRepo;
        this.userRepo = userRepo;
        this.nutritionService = nutritionService;
    }
    /**Displays nutrition data for the selecte data
     * 
     * Shows:
     * Daily totals (calorie, macronutrients)
     * Recent food entries
     * Weekly navigation(previous and next week)
     */
    @GetMapping
    public String list(@RequestParam(required = false) String date, Model model, Principal principal) {
        //Get current user
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        //What is the selected date, default today
        LocalDate selectedDate;
        if (date != null && !date.isBlank()) {
            selectedDate = LocalDate.parse(date);
        } else {
            selectedDate = LocalDate.now();
        }
        //Get enries for the selected date
        List<FoodEntry> todaysEntries = nutritionService.getEntriesForDate(user, selectedDate);
        //Calulate daily totals using nutritionService
        int calories = nutritionService.getCaloriesFromEntries(todaysEntries);
        int protein = nutritionService.getProteinFromEntries(todaysEntries);
        int carbs = nutritionService.getCarbsFromEntries(todaysEntries);
        int fat = nutritionService.getFatsFromEntries(todaysEntries);
        int sugar = nutritionService.getSugarFromEntries(todaysEntries);
        int salt = nutritionService.getSaltFromEntries(todaysEntries);
        //Get weekly nav data
        LocalDate startOfWeek = nutritionService.getStartOfWeek(selectedDate);
        List<LocalDate> weekDays = nutritionService.getWeekDays(selectedDate);

        LocalDate previousWeek = startOfWeek.minusWeeks(1);
        LocalDate nextWeek = startOfWeek.plusWeeks(1);
        //limit displayed entities for clean ui
        List<FoodEntry> recentEntries = todaysEntries.stream().limit(10).toList();

        //pass data to view
        model.addAttribute("entries", recentEntries);
        model.addAttribute("recentEntries", recentEntries);
        model.addAttribute("totalCalories", calories);
        model.addAttribute("totalCarbs", carbs);
        model.addAttribute("totalProtein", protein);
        model.addAttribute("totalFats", fat);
        model.addAttribute("totalSugar", sugar);
        model.addAttribute("totalSalt", salt);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("previousWeek", previousWeek);
        model.addAttribute("nextWeek", nextWeek);

        return "nutrition/list";

    }
    //Displays form for making new food entry
    @GetMapping("/new")
    public String newEntry(Model model) {
        //Create a new food entry with timestamp now
        FoodEntry entry = new FoodEntry();
        entry.setEntryDateTime(LocalDateTime.now());

        model.addAttribute("entry", entry);
        model.addAttribute("formTitle", "Add food entry");
        model.addAttribute("formAction", "/nutrition");
        model.addAttribute("submitText", "Save entry");

        return "nutrition/new";
    }
    // Handles the creation of a new food entry
    @PostMapping
    public String create(@ModelAttribute("entry") FoodEntry entry, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
        //Basic validation
        if (entry.getMealType() == null || entry.getMealType().isBlank()) {
            throw new IllegalArgumentException("Meal type is required");
        }
        if (entry.getFoodName() == null || entry.getFoodName().isBlank()) {
            throw new IllegalArgumentException("Food name is required");
        }
        //Make sure timestamp is set
        if (entry.getEntryDateTime() == null) {
            entry.setEntryDateTime(LocalDateTime.now());
        }
        //Default null numerics to 0
        if (entry.getCalories() == null)
            entry.setCalories(0);
        if (entry.getCarbs() == null)
            entry.setCarbs(0);
        if (entry.getProtein() == null)
            entry.setProtein(0);
        if (entry.getFats() == null)
            entry.setFats(0);
        if (entry.getSugar() == null)
            entry.setSugar(0);
        if (entry.getSalt() == null)
            entry.setSalt(0);

        entry.setUser(user);

        foodEntryRepo.save(entry);

        return "redirect:/nutrition";
    }
    // Displays edit form for existing food entries
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        FoodEntry entry = requireOwnedEntry(id, principal);

        model.addAttribute("entry", entry);
        model.addAttribute("formTitle", "Edit food entry");
        model.addAttribute("formAction", "/nutrition/" + id + "/edit");
        model.addAttribute("submitText", "Update entry");

        return "nutrition/new";
    }
    //Handles the submission fo edited food entry
    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
            @ModelAttribute("entry") FoodEntry updated,
            Principal principal) {

        FoodEntry existing = requireOwnedEntry(id, principal);
        //validate input
        if (updated.getMealType() == null || updated.getMealType().isBlank()) {
            throw new IllegalArgumentException("Meal type is required");
        }
        if (updated.getFoodName() == null || updated.getFoodName().isBlank()) {
            throw new IllegalArgumentException("Food name is required");
        }
        if (updated.getEntryDateTime() == null) {
            updated.setEntryDateTime(existing.getEntryDateTime());
        }
        //update existing fields
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
    //Delete a food entry
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        FoodEntry entry = requireOwnedEntry(id, principal);
        foodEntryRepo.delete(entry);
        return "redirect:/nutrition";
    }
    //Ensures foof entry belongs to current user
    //Prevents user from deleting or modifying another users food entry
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
