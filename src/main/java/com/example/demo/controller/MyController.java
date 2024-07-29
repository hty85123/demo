package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.MemberRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.MemberUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
public class MyController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            var auth = authenticationManager.authenticate(token);
            var user = (MemberUserDetails) auth.getPrincipal();

            String accessToken = jwtService.createAccessToken(user);
            String refreshToken = jwtService.createRefreshToken(user);

            return ResponseEntity.ok(LoginResponse.of(accessToken, refreshToken, user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

    @PostMapping("/members")
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @DeleteMapping("/members/{username}")
    public ResponseEntity<Void> deleteMember(@PathVariable String username) {
        memberRepository.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/members/{username}")
    public ResponseEntity<Member> updateMember(@PathVariable String username, @RequestBody Member updatedMember) {
        Optional<Member> existingMember = memberRepository.findByUsername(username);
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            member.setPassword(updatedMember.getPassword());
            member.setNickname(updatedMember.getNickname());
            member.setAuthorities(updatedMember.getAuthorities());
            memberRepository.save(member);
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/members/{username}")
    public ResponseEntity<Member> getMember(@PathVariable String username) {
        Optional<Member> member = memberRepository.findByUsername(username);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return ResponseEntity.ok(members);
    }}
