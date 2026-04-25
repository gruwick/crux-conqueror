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

@Service
public class NutritionService {
    private final FoodEntryRepo foodEntryRepo;

    public NutritionService(FoodEntryRepo foodEntryRepo) {
        this.foodEntryRepo = foodEntryRepo;
    }

    public List<FoodEntry> getTodaysEntries(User user) {
        return getEntriesForDate(user, LocalDate.now());
    }

    public List<FoodEntry> getEntriesForDate(User user, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return foodEntryRepo.findByUserAndEntryDateTimeBetweenOrderByEntryDateTimeDesc(user, startOfDay, endOfDay);
    }

    public LocalDate getStartOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    public List<LocalDate> getWeekDays(LocalDate selectedDate) {
        LocalDate startOfWeek = getStartOfWeek(selectedDate);
        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(startOfWeek.plusDays(i));
        }
        return weekDays;
    }

    public int getCaloriesFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getCalories)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getProteinFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getProtein)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getCarbsFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getCarbs)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getFatsFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getFats)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getSaltFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getSalt)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getSugarFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getSugar)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public String getLatestMeal(User user) {
        return foodEntryRepo.findFirstByUserOrderByEntryDateTimeDesc(user)
                .map(FoodEntry::getFoodName)
                .orElse("No meals have been logged");
    }

}