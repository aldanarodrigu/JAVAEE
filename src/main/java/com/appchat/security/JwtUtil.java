package com.appchat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String JWT_SECRET = System.getenv("JWT_SECRET");
    private static final Key KEY;

    static {
        if (JWT_SECRET == null || JWT_SECRET.trim().isEmpty()) {
            throw new IllegalStateException("Falta la variable de entorno JWT_SECRET");
        }
        if (JWT_SECRET.length() < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public static Key getKey() {
        return KEY;
    }

    private static Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String generarToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(KEY)
                .compact();
    }

    public static boolean esTokenValido(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String obtenerEmail(String token) {
        return parse(token).getSubject();
    }

    public static Long getUserIdFromToken(String token) {
        return parse(token).get("userId", Long.class);
    }
}