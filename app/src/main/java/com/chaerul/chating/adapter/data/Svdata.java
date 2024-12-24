package com.chaerul.chating.adapter.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Svdata {
    private final SharedPreferences sharedPreferences;

    public Svdata(Context context){
        sharedPreferences =context.getSharedPreferences("user_data", context.MODE_PRIVATE);
    }
    public String getUserEmail(){
        return sharedPreferences.getString("user_email", "");
    }
    public String getUserName(){
        return sharedPreferences.getString("user_name", "");
    }

    public boolean isLoggedin(){
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    public void reset(){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("user_email", "");
        edit.putString("user_name", "");
        edit.putBoolean("is_logged_in", false);
        edit.apply();
    }
}
