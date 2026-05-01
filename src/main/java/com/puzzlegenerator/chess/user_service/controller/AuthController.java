package com.puzzlegenerator.chess.user_service.controller;

import com.puzzlegenerator.chess.user_service.dto.request.LoginRequest;
import com.puzzlegenerator.chess.user_service.dto.request.RefreshTokenRequest;
import com.puzzlegenerator.chess.user_service.dto.request.RegisterRequest;
import com.puzzlegenerator.chess.user_service.dto.response.LoginResponse;
import com.puzzlegenerator.chess.user_service.dto.response.RegisterResponse;
import com.puzzlegenerator.chess.user_service.dto.response.TokenResponse;
import com.puzzlegenerator.chess.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for: {}", request.getEmail());
        RegisterResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for: {}", request.getEmail());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request");
        TokenResponse response = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
