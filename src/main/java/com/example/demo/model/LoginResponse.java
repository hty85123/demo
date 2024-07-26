package com.example.demo.model;

import com.example.demo.security.MemberUserDetails;

import java.util.List;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String id;
    private String username;
    private String nickname;
    private List<MemberAuthority> authorities;

    public static LoginResponse of(String accessToken,String refreshToken, MemberUserDetails user) {
        var res = new LoginResponse();
        res.accessToken = accessToken;
        res.refreshToken = refreshToken;
        res.id = user.getId();
        res.username = user.getUsername();
        res.nickname = user.getNickname();
        res.authorities = user.getMemberAuthorities();

        return res;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public List<MemberAuthority> getAuthorities() {
        return authorities;
    }
}