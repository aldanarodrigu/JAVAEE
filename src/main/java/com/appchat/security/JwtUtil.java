package com.appchat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import static javax.crypto.Cipher.SECRET_KEY;

public class JwtUtil {

    private static final Key KEY = Keys.hmacShaKeyFor(
        "claveSuperSecreta12345678901234567890".getBytes()
    );
    
    public static Key getKey() {
        return KEY;
    }

    public static String generarToken(Long userId, String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                .signWith(KEY)
                .compact();
    }
    
    public static boolean esTokenValido(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String obtenerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("userId", Long.class); 

        } catch (Exception e) {
            throw new RuntimeException("Token inválido", e);
        }
    }
    
}
