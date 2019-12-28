package com.Get_to_know_your_city_again.model;

public class Users {

    private String user_id;
    private String username;
    private String email;

    public Users(String user_id, String username, String email) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
    }

    public Users(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
}
