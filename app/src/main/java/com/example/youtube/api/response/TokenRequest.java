package com.example.youtube.api.response;

public class TokenRequest {
    private String userId;

    public TokenRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
