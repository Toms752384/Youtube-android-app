package com.example.youtube.api;

import com.example.youtube.model.User;

import java.util.List;

public class UsersResponse {
    private String message;
    private List<User> users;

    // Getters and Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
