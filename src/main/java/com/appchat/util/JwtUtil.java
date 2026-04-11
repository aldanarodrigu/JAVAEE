package com.appchat.util;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtUtil {

    // TODO: configure secret key and token expiration
    private static final String SECRET_KEY_PLACEHOLDER = "CHANGE_ME";
    private static final long EXPIRATION_MS = 3600000L;
}
