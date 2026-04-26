package com.cruxconqueror.crux_conqueror.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
/**
 * Entity representing user account
 * 
 * Stores authentication details
 * Profile information
 * personalised nutrition
 * Activity settings
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Long id;
    //Unique username for login
    @Column(name = "Usernames", nullable = false, unique = true)
    private String username;
    //Unique email address
    @Column(name = "Emails", nullable = false, unique = true)
    private String email;
    //Sored passwords in BCrypt
    @Column(name = "Password_Hash", nullable = false)
    private String passwordHash;
    //Timestamp when account was made
    @Column(name = "Date_Made", insertable = false, updatable = false)
    private LocalDateTime dateMade;

    @Column(name = "Bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "Age")
    private Integer age;

    @Column(name = "Height_cm")
    private Double heightCm;

    @Column(name = "Weight_kg")
    private Double weightKg;

    @Column(name = "Experience_Level", length = 30)
    private String experienceLevel;

    @Column(name = "Goal_Type", length = 30)
    private String goalType;

    @Column(name = "Activity_Level", length = 30)
    private String activityLevel;

    @Column(name = "Bio_Visibility", length = 20)
    private String bioVisibility;

    @Column(name = "Age_Visibility", length = 20)
    private String ageVisibility;

    @Column(name = "Height_Visibility", length = 20)
    private String heightVisibility;

    @Column(name = "Weight_Visibility", length = 20)
    private String weightVisibility;

    @Column(name = "Experience_Visibility", length = 20)
    private String experienceVisibility;
    //Determins if targets are calulated automatically or manually set
    @Column(name = "Target_Mode", length = 20)
    private String targetMode;

    @Column(name = "Calorie_Goal")
    private Integer calorieGoal;

    @Column(name = "Protein_Goal")
    private Integer proteinGoal;

    @Column(name = "Carb_Goal")
    private Integer carbGoal;

    @Column(name = "Fat_Goal")
    private Integer fatGoal;
    //Default constructor required by JPA
    public User() {
    }

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    //Getters and setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getDateMade() {
        return dateMade;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getBioVisibility() {
        return bioVisibility;
    }

    public void setBioVisibility(String bioVisibility) {
        this.bioVisibility = bioVisibility;
    }

    public String getAgeVisibility() {
        return ageVisibility;
    }

    public void setAgeVisibility(String ageVisibility) {
        this.ageVisibility = ageVisibility;
    }

    public String getHeightVisibility() {
        return heightVisibility;
    }

    public void setHeightVisibility(String heightVisibility) {
        this.heightVisibility = heightVisibility;
    }

    public String getWeightVisibility() {
        return weightVisibility;
    }

    public void setWeightVisibility(String weightVisibility) {
        this.weightVisibility = weightVisibility;
    }

    public String getExperienceVisibility() {
        return experienceVisibility;
    }

    public void setExperienceVisibility(String experienceVisibility) {
        this.experienceVisibility = experienceVisibility;
    }

    public String getTargetMode() {
        return targetMode;
    }

    public void setTargetMode(String targetMode) {
        this.targetMode = targetMode;
    }

    public Integer getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(Integer calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public Integer getProteinGoal() {
        return proteinGoal;
    }

    public void setProteinGoal(Integer proteinGoal) {
        this.proteinGoal = proteinGoal;
    }

    public Integer getCarbGoal() {
        return carbGoal;
    }

    public void setCarbGoal(Integer carbGoal) {
        this.carbGoal = carbGoal;
    }

    public Integer getFatGoal() {
        return fatGoal;
    }

    public void setFatGoal(Integer fatGoal) {
        this.fatGoal = fatGoal;
    }
}