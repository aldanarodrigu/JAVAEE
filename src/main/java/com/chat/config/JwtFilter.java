package com.chat.config;

import com.chat.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Set;

@Provider
public class JwtFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final Set<String> OPEN_PATHS = Set.of(
        "users/register",
        "users/login"
    );

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (isOpenPath(path)) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Missing or invalid Authorization header\"}")
                    .build()
            );
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtUtil.isValid(token)) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid or expired token\"}")
                    .build()
            );
        }
    }

    private boolean isOpenPath(String path) {
        // getPath() returns the path relative to the application root (without /api prefix)
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        return OPEN_PATHS.contains(normalizedPath);
    }
}
