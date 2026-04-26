package com.cruxconqueror.crux_conqueror.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;
/**
 * This Service layer is for handling nutrition related logic
 * 
 * Seperated the businees logic from controllers
 * includes:
 * Date filtering
 * Weekly calculations
 * Nutritional aggregation
 */
@Service
public class NutritionService {
    private final FoodEntryRepo foodEntryRepo;

    public NutritionService(FoodEntryRepo foodEntryRepo) {
        this.foodEntryRepo = foodEntryRepo;
    }
    //returns all entrys for current day
    public List<FoodEntry> getTodaysEntries(User user) {
        return getEntriesForDate(user, LocalDate.now());
    }
    /**returns entries for specific date
     * used stand and end range to capture full day
     */
    public List<FoodEntry> getEntriesForDate(User user, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return foodEntryRepo.findByUserAndEntryDateTimeBetweenOrderByEntryDateTimeDesc(user, startOfDay, endOfDay);
    }
    //returns the start of the week, Monday
    public LocalDate getStartOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }
    /** returns the dates within the selected week
     * used for weekly navigation and UI display
     */
    public List<LocalDate> getWeekDays(LocalDate selectedDate) {
        LocalDate startOfWeek = getStartOfWeek(selectedDate);
        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(startOfWeek.plusDays(i));
        }
        return weekDays;
    }
    // Aggregates total calories from a list of entries
    public int getCaloriesFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getCalories)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    //Aggregates total protein from entries
    public int getProteinFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getProtein)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    //Aggregates total carbs from entries
    public int getCarbsFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getCarbs)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    //Aggregates total fats from entries
    public int getFatsFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getFats)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    //Aggregates total salt from entries
    public int getSaltFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getSalt)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    //Aggregates total sugar from entries 
    public int getSugarFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getSugar)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    //Most recently logged in meal
    public String getLatestMeal(User user) {
        return foodEntryRepo.findFirstByUserOrderByEntryDateTimeDesc(user)
                .map(FoodEntry::getFoodName)
                .orElse("No meals have been logged");
    }

}