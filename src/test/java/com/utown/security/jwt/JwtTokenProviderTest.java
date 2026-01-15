package com.utown.security.jwt;

import com.utown.config.Jwtproperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        Jwtproperties  jwtProperties = new Jwtproperties();

        jwtProperties.setSecret("MySecretKeyForJWT2025UtownProjectVerySecureAndLongString");
        jwtProperties.setAccessTokenExpiration(900000L);
        jwtProperties.setRefreshTokenExpiration(604800000L);

        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    @Test
    void generateAccessToken_shouldReturnValidToken() {
        Long userId = 1L;
        String role = "admin";

        String token = jwtTokenProvider.generateAccessToken(userId,role);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(role, jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    void generateRefreshToken_shouldReturnValidToken() {
        Long userId = 1L;

        String token = jwtTokenProvider.generateRefreshToken(userId);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }
}
