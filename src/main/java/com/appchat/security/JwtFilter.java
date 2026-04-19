package com.appchat.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {

        String path = requestContext.getUriInfo().getPath();

        if (path.contains("auth/login")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(401)
                    .entity("Falta token")
                    .build());
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();

            requestContext.setProperty("email", email);

        } catch (Exception e) {
            requestContext.abortWith(Response.status(401)
                    .entity("Token inválido")
                    .build());
        }
    }
}