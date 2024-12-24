package com.chaerul.chating.adapter.data;

public class ListChatUsers {
    String name;
    String email;
    long lastOnline;
    String isOnline;
    String profile;
    String userId;

    public ListChatUsers(){}

    public ListChatUsers(String userId,String name, String email, long lastOnline, String isOnline, String profile){
        this.userId = userId;
        this.name= name;
        this.email=email;
        this.lastOnline=lastOnline;
        this.isOnline=isOnline;
        this.profile=profile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

}
