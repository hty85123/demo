package com.example.demo.model;

public class TokenRefreshResponse {
    private String accessToken;

    public TokenRefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}