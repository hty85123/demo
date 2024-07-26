package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.TokenRefreshRequest;
import com.example.demo.model.TokenRefreshResponse;
import com.example.demo.security.JwtService;
import com.example.demo.security.MemberUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MyController {
//    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService; // 注入 UserDetailsService

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            var auth = authenticationManager.authenticate(token);
            var user = (MemberUserDetails) auth.getPrincipal();

            String accessToken = jwtService.createAccessToken(user);
            String refreshToken = jwtService.createRefreshToken(user);

            return ResponseEntity.ok(LoginResponse.of(accessToken, refreshToken, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            // 驗證 Refresh Token
            Claims claims = jwtService.parseToken(request.getRefreshToken(), true);
            String username = claims.get("username", String.class);

            // 創建新的 Access Token
            MemberUserDetails userDetails = (MemberUserDetails) userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtService.createAccessToken(userDetails);

            return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

//    @GetMapping("/who-am-i")
//    public Map<String, Object> whoAmI(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
//        var jwt = authorization.substring(BEARER_PREFIX.length());
//        try {
//            return jwtService.parseToken(jwt, false);
//        } catch (JwtException e) {
//            throw new BadCredentialsException(e.getMessage(), e);
//        }
//    }

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ("anonymousUser".equals(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("你尚未經過身份認證");
        }

        var userDetails = (MemberUserDetails) principal;
        String response = String.format("嗨，你的編號是%s%n帳號：%s%n暱稱：%s%n權限：%s",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getNickname(),
                userDetails.getAuthorities());
        return ResponseEntity.ok(response);
    }
}
