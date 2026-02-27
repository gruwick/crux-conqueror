package com.cruxconqueror.crux_conqueror.dto;

public class LeaderboardRow {

    private String username;
    private int sessionsLast30;
    private int minutesLast30;
    private double avgIntensityLast30;

    public LeaderboardRow(String username, int sessionsLast30, int minutesLast30, double avgIntensityLast30) {
        this.username = username;
        this.sessionsLast30 = sessionsLast30;
        this.minutesLast30 = minutesLast30;
        this.avgIntensityLast30 = avgIntensityLast30;
    }

    public String getUsername() { return username; }
    public int getSessionsLast30() { return sessionsLast30; }
    public int getMinutesLast30() { return minutesLast30; }
    public double getAvgIntensityLast30() { return avgIntensityLast30; }
}