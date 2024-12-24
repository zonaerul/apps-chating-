package com.chaerul.chating.adapter.data;

public class StatusUsers {
    private String url_photo_or;
    private String type_status; //foto atau video
    private String user_name;
    private String user_email;

    public StatusUsers(String url_photo_or, String type_status, String user_email, String user_name){
        this.url_photo_or = url_photo_or;
        this.type_status = type_status;
        this.user_email = user_email;
        this.user_name = user_name;
    }

    public String getTypeStatus() {
        return type_status;
    }

    public String getUrlPhotoOrVideo() {
        return url_photo_or;
    }

    public String getUserEmail() {
        return user_email;
    }

    public String getUserName() {
        return user_name;
    }
}
