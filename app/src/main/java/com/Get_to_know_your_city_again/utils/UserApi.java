package com.Get_to_know_your_city_again.utils;

import android.app.Application;

import com.firebase.ui.auth.data.model.User;


// singleton

public class UserApi extends Application {

    private String username;
    private String userId;
    private String email;
    private static UserApi instance;


    public static UserApi getInstance(){
        if(instance == null)
            instance = new UserApi();

        return instance;
    }

    public UserApi(){  //should be private in singleton

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
