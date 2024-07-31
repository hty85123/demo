package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class JwtService {
    private final SecretKey secretKey;
    private final SecretKey refreshSecretKey;
    private final int validSeconds;
    private final int refreshValidSeconds;

    public JwtService(String secretKeyStr, String refreshSecretKeyStr, int validSeconds, int refreshValidSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecretKeyStr.getBytes());
        this.validSeconds = validSeconds;
        this.refreshValidSeconds = refreshValidSeconds;
    }

    public String createAccessToken(MemberUserDetails user) {
        var expirationMillis = Instant.now()
                .plusSeconds(validSeconds)
                .getEpochSecond()
                * 1000;

        var claims = Jwts.claims()
                .subject(user.getId())
                .issuedAt(new Date())
                .expiration(new Date(expirationMillis))
                .add("username", user.getUsername())
                .add("authorities", user.getMemberAuthorities())
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(MemberUserDetails user) {
        var expirationMillis = Instant.now()
                .plusSeconds(refreshValidSeconds)
                .getEpochSecond()
                * 1000;

        var claims = Jwts.claims()
                .subject(user.getId())
                .issuedAt(new Date())
                .expiration(new Date(expirationMillis))
                .add("username", user.getUsername())
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(refreshSecretKey)
                .compact();
    }

    public Claims parseToken(String jwt, boolean isRefreshToken) throws JwtException {
        SecretKey key = isRefreshToken ? refreshSecretKey : secretKey;
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    }
}