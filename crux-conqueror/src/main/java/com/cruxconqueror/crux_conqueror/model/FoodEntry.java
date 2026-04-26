package com.cruxconqueror.crux_conqueror.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
/**
 * Entity representing food entry logged by a user
 * 
 * Stores macronutrient data for specific meals
 * incldes calories and optional notes
 */
@Entity
@Table(name = "Food_Entries")

public class FoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Entry_ID")
    private Long id;
    //Each food entry belongs to a specific user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;
    //Date and time food was logged
    @Column(name = "Entry_Date_Time", nullable = false)
    private LocalDateTime entryDateTime;
    //Type of meal
    @Column(name = "Meal_Type", nullable = false, length = 20)
    private String mealType;
    //Name desciption
    @Column(name = "Food_Name", nullable = false, length = 50)
    private String foodName;
    //nutritional values, must be greater than or equal to 0
    @Min(0)
    @Column(name = "Calories")
    private Integer calories;

    @Min(0)
    @Column(name = "Carbs")
    private Integer carbs;

    @Min(0)
    @Column(name = "Protein")
    private Integer protein;

    @Min(0)
    @Column(name = "Fats")
    private Integer fats;

    @Min(0)
    @Column(name = "Sugar")
    private Integer sugar;

    @Min(0)
    @Column(name = "Salt")
    private Integer salt;
    //Optional user notes on meal
    @Column(name = "Additional_Thoughts", columnDefinition = "TEXT")
    private String additionalThoughts;
    //Default construcor required by JPA
    public FoodEntry() {
    }
    //Getters and setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getEntryDateTime() {
        return entryDateTime;
    }

    public void setEntryDateTime(LocalDateTime entryDateTime) {
        this.entryDateTime = entryDateTime;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getCarbs() {
        return carbs;
    }

    public void setCarbs(Integer carbs) {
        this.carbs = carbs;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFats() {
        return fats;
    }

    public void setFats(Integer fats) {
        this.fats = fats;
    }

    public Integer getSugar() {
        return sugar;
    }

    public void setSugar(Integer sugar) {
        this.sugar = sugar;
    }

    public Integer getSalt() {
        return salt;
    }

    public void setSalt(Integer salt) {
        this.salt = salt;
    }

    public String getAdditionalThoughts() {
        return additionalThoughts;
    }

    public void setAdditionalThoughts(String additional_Throughts) {
        this.additionalThoughts = additional_Throughts;
    }

}
