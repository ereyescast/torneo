package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.request.LoginRequest;
import com.torneo.copaestudiantil.dto.request.RegisterRequest;
import com.torneo.copaestudiantil.dto.request.RegistroDelegadoRequest;
import com.torneo.copaestudiantil.dto.response.AuthResponse;
import com.torneo.copaestudiantil.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "01. Autenticación", description = "Registro y login de usuarios")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Retorna token JWT para usar en los demás endpoints")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(authService.login(request), CodigoNegocio.S_AUT_200_001));
    }

    @Operation(summary = "Registrar usuario", description = "Crea usuario y retorna su token JWT")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(authService.register(request),
                        CodigoNegocio.S_AUT_201_001));
    }

    @Operation(summary = "Registrar delegado con código",
            description = "El delegado se registra con el código que le dio el organizador.")
    @PostMapping("/registro-delegado")
    public ResponseEntity<ApiResponse<AuthResponse>> registroDelegado(
            @Valid @RequestBody RegistroDelegadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(authService.registrarDelegado(request),
                        CodigoNegocio.S_AUT_201_001));
    }
}
