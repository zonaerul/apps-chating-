package com.chaerul.chating.adapter.data;

public class Users {
    public String userId;
    public String name;
    public String profile;
    public String createdAt;
    public String email;
    public String password;
    public String lastOnline;
    public boolean isOnline;

    // Constructor
    public Users(String userId, boolean isOnline, String name, String profile, String email, String password, String createdAt, String lastOnline) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
        this.createdAt = createdAt;
        this.email = email;
        this.password = password;
        this.lastOnline = lastOnline;
        this.isOnline = isOnline;
    }

    // Setters
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getters
    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
