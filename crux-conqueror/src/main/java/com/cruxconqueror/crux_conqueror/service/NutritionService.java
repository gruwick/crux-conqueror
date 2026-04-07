package com.cruxconqueror.crux_conqueror.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.FoodEntryRepo;

@Service
public class NutritionService {
    private final FoodEntryRepo foodEntryRepo;

    public NutritionService(FoodEntryRepo foodEntryRepo){
        this.foodEntryRepo = foodEntryRepo;
    }
    public List<FoodEntry> getTodaysEntries(User user){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return foodEntryRepo.findByUserAndEntryDateTimeBetweenOrderByEntryDateTimeDesc( user, startOfDay, endOfDay);
    }

    public int getCaloriesFromEntries(List<FoodEntry> entries){
        return entries.stream()
                .map(FoodEntry::getCalories)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();    
}
    public int getProteinFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getProtein)
                .filter(v -> v !=null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    public int getCarbsFromEntries(List<FoodEntry> entries) {
        return entries.stream()
                .map(FoodEntry::getCarbs)
                .filter(v -> v !=null)
                .mapToInt(Integer::intValue)
                .sum();
    }
    public String getLatestMeal(User user) {
        return foodEntryRepo.findFirstByUserOrderByEntryDateTimeDesc(user)
                .map(FoodEntry::getFoodName)
                .orElse("No meals have been logged");
    }
}