package com.example.eduscience.model;

public class Leaderboard {
    String username, userId;

    public Leaderboard() {
    }

    public Leaderboard(String username, String userId, int totalMark) {
        this.username = username;
        this.userId = userId;
        this.totalMark = totalMark;
    }

    int totalMark;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(int totalMark) {
        this.totalMark = totalMark;
    }
}
