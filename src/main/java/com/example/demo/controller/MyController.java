package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.security.JwtService;
import com.example.demo.security.MemberUserDetails;
import com.example.demo.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MyController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private LogoutHandler customLogoutHandler;

    @Autowired
    private LogoutSuccessHandler customLogoutSuccessHandler;

    // Handle user login, generate and return JWT tokens
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var auth = authenticationManager.authenticate(token);
        var user = (MemberUserDetails) auth.getPrincipal();

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        return ResponseEntity.ok(LoginResponse.of(accessToken, refreshToken, user));
    }

    //Logout user, generate and return empty JWT tokens
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // Refresh JWT token
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        Claims claims = jwtService.parseToken(request.getRefreshToken(), true);
        String username = claims.get("username", String.class);

        MemberUserDetails userDetails = (MemberUserDetails) userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.createAccessToken(userDetails);

        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken));
    }

    // Return home page information with user details if authenticated
    @GetMapping("/home")
    public ResponseEntity<String> home() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ("anonymousUser".equals(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        var userDetails = (MemberUserDetails) principal;
        String response = String.format("Hi, your ID is %s%nUsername: %s%nAuthorities: %s",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities());
        return ResponseEntity.ok(response);
    }

    // Add a new member
    @PostMapping("/members")
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        Member createdMember = memberService.addMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    // Delete a member by username
    @DeleteMapping("/members/{username}")
    public ResponseEntity<Void> deleteMember(@PathVariable String username) {
        memberService.deleteMember(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Update a member by username
    @PutMapping("/members/{username}")
    public ResponseEntity<Member> updateMember(@PathVariable String username, @RequestBody Member updatedMember) {
        Member member = memberService.updateMember(username, updatedMember);
        return ResponseEntity.ok(member);
    }

    // Get a member by username
    @GetMapping("/members/{username}")
    public ResponseEntity<Member> getMember(@PathVariable("username") String username) {
        Member member = memberService.getMemberByUsername(username);
        return ResponseEntity.ok(member);

    }

    // Get all members
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }
}