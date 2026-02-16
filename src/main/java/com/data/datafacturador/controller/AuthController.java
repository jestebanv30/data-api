package com.data.datafacturador.controller;

import com.data.datafacturador.dto.LoginRequest;
import com.data.datafacturador.dto.RefreshTokenRequest;
import com.data.datafacturador.dto.LoginResponse;
import com.data.datafacturador.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación
 * Endpoints: /api/auth/*
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Autentica un usuario y retorna tokens JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request recibido para usuario: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/refresh
     * Refresca el access token usando un refresh token válido
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request recibido");
        Map<String, Object> response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/logout
     * Cierra la sesión del usuario
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        log.info("Logout request recibido");
        Map<String, Object> response = authService.logout();
        return ResponseEntity.ok(response);
    }
}
