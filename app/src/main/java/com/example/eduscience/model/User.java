package com.example.eduscience.model;

public class User {
    public String username, email, role, imgUrl;
    public int totalMark;

    public User() {
    }

    public User(String username, String email, String role, int totalMark, String imgUrl) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.totalMark = totalMark;
        this.imgUrl = imgUrl;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(int totalMark) {
        this.totalMark = totalMark;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
