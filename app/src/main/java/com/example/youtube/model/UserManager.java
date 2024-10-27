package com.example.youtube.model;


import java.util.List;
import java.util.ArrayList;

public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private String token;

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void clearCurrentUser() {
        this.currentUser = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clearToken(){
        this.token = null;
    }
}