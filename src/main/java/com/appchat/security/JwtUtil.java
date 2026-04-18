package com.appchat.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final Key KEY = Keys.hmacShaKeyFor(
        "claveSuperSecreta12345678901234567890".getBytes()
    );
    
    public static Key getKey() {
        return KEY;
    }

    public static String generarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                .signWith(KEY)
                .compact();
    }
}