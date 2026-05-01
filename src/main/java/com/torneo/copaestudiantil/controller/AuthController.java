package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.LoginRequest;
import com.torneo.copaestudiantil.dto.request.RegisterRequest;
import com.torneo.copaestudiantil.dto.response.AuthResponse;
import com.torneo.copaestudiantil.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Body: { "email": "admin@torneo.com", "password": "12345678" }
     * Retorna: { "token": "eyJ...", "tipo": "Bearer", "rol": "ADMIN", ... }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/auth/register
     * Body: { "nombre": "Juan", "email": "juan@torneo.com", "password": "12345678", "rol": "ORGANIZADOR" }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}
