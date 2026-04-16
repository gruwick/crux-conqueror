package com.cruxconqueror.crux_conqueror.dto;

public class LeaderboardRow {

    private String username;
    private int sessionsLast30;
    private int minutesLast30;
    private double avgIntensityLast30;
    private String bestGrade;
    private int bestGradeScore;

    public LeaderboardRow(String username, int sessionsLast30, int minutesLast30, double avgIntensityLast30, String bestGrade, int bestGradeScore) {
        this.username = username;
        this.sessionsLast30 = sessionsLast30;
        this.minutesLast30 = minutesLast30;
        this.avgIntensityLast30 = avgIntensityLast30;
        this.bestGrade = bestGrade;
        this.bestGradeScore = bestGradeScore;
    }
    public String getUsername() { return username; }
    public int getSessionsLast30() { return sessionsLast30; }
    public int getMinutesLast30() { return minutesLast30; }
    public double getAvgIntensityLast30() { return avgIntensityLast30; }
    public String getBestGrade() { return bestGrade; }
    public int getBestGradeScore() { return bestGradeScore; }
}