package com.puzzlegenerator.chess.user_service.service;

import com.puzzlegenerator.chess.user_service.model.User;
import com.puzzlegenerator.chess.user_service.model.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "chess-puzzle-platform-secret-key-min-256-bits-long-enough",
                3600000,
                604800000
        );
    }

    @Test
    void shouldGenerateAndValidateToken() {
        User user = User.builder()
                .id("user-123")
                .username("testuser")
                .role(UserRole.USER)
                .build();

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldExtractUserIdFromToken() {
        User user = User.builder()
                .id("user-456")
                .username("testuser")
                .role(UserRole.USER)
                .build();

        String token = jwtService.generateToken(user);
        assertEquals("user-456", jwtService.extractUserId(token));
    }

    @Test
    void shouldExtractClaimsFromToken() {
        User user = User.builder()
                .id("user-789")
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractClaims(token);

        assertEquals("user-789", claims.getSubject());
        assertEquals("admin", claims.get("username"));
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtService.validateToken("invalid.token.here"));
    }

    @Test
    void shouldGenerateRefreshToken() {
        User user = User.builder()
                .id("user-123")
                .username("testuser")
                .role(UserRole.USER)
                .build();

        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(refreshToken);
        assertTrue(jwtService.validateToken(refreshToken));
    }
}
