package com.los.administration.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }



    // ⚠ Minimum 32 chars for HS256
    // private static final String SECRET =
   //          "CHANGE_ME_LATER_CHANGE_ME_LATER_CHANGE_ME_LATER";

   // private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String userId, String role) {

        return Jwts.builder()
                .setSubject(userId)                 // ✅ FIXED
                .claim("role", role)
                .setIssuedAt(new Date())             // ✅ FIXED
                .setExpiration(
                        new Date(System.currentTimeMillis() + 86400000)
                )                                   // ✅ FIXED
                .signWith(key)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String extractUserId(String token) {
        return validateToken(token).getBody().getSubject();
    }

    public String extractRole(String token) {
        return validateToken(token).getBody().get("role", String.class);
    }
}
