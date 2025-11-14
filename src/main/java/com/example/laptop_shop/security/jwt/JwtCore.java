package com.example.laptop_shop.security.jwt;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtCore {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = createSecretKey(jwtSecret);
    }

    public SecretKey createSecretKey(String jwtSecret) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return secretKey;
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                // .claim("role", role)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Claims claims = getClaims(token);
            if (claims.getExpiration().after(new Date()))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
