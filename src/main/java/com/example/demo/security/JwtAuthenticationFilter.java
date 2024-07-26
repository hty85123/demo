package com.example.demo.security;

import com.example.demo.model.MemberAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 取得 header
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析 JWT
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        Claims claims;
        try {
            claims = jwtService.parseToken(jwt, false);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 建立 UserDetails 物件
        var userDetails = new MemberUserDetails();
        userDetails.setId(claims.getSubject());
        userDetails.setUsername(claims.get("username", String.class));
        userDetails.setNickname(claims.get("nickname", String.class));

        var authorities = claims.get("authorities");
        List<String> memberAuthorities;
        if (authorities instanceof List<?>) {
            memberAuthorities = ((List<?>) authorities).stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .toList();
        } else {
            throw new IllegalArgumentException("Invalid authorities format");
        }

        var memberAuthoritiesEnum = memberAuthorities.stream()
                .map(MemberAuthority::valueOf)
                .toList();
        userDetails.setMemberAuthorities(memberAuthoritiesEnum);

        // 放入 Security Context
        var token = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(token);

        filterChain.doFilter(request, response);
    }
}
