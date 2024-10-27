package com.example.youtube.api;

import com.example.youtube.model.User;

import java.util.List;

public class SingleUserResponse {
    private String message;
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }
    public void setUsers(User user) { this.user = user; }

}
