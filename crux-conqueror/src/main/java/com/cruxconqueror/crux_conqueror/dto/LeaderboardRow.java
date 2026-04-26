package com.cruxconqueror.crux_conqueror.dto;
/**
 * Data transfer Object (DTO) for leaderboard views
 * 
 * Stored aggregated user stats for display on the leaderboard
 * rather than exposing full training session model directly
 */
public class LeaderboardRow {

    private String username;
    private int sessionsLast30;
    private int minutesLast30;
    private double avgIntensityLast30;
    private String bestGrade;
    private int bestGradeScore;
    //Constucts a leaderboard row ith aggregated user data
    public LeaderboardRow(String username, int sessionsLast30, int minutesLast30, double avgIntensityLast30,
            String bestGrade, int bestGradeScore) {
        this.username = username;
        this.sessionsLast30 = sessionsLast30;
        this.minutesLast30 = minutesLast30;
        this.avgIntensityLast30 = avgIntensityLast30;
        this.bestGrade = bestGrade;
        this.bestGradeScore = bestGradeScore;
    }
    //Getters sued by Thymelead to display leaderboard values
    public String getUsername() {
        return username;
    }

    public int getSessionsLast30() {
        return sessionsLast30;
    }

    public int getMinutesLast30() {
        return minutesLast30;
    }

    public double getAvgIntensityLast30() {
        return avgIntensityLast30;
    }

    public String getBestGrade() {
        return bestGrade;
    }

    public int getBestGradeScore() {
        return bestGradeScore;
    }
}