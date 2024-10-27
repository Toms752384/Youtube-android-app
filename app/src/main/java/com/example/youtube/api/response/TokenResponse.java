package com.example.youtube.api.response;

import com.example.youtube.model.User;

import java.util.List;

public class TokenResponse {
    private String message;
    private String token;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
