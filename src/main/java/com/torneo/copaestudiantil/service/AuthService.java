package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.LoginRequest;
import com.torneo.copaestudiantil.dto.request.RegisterRequest;
import com.torneo.copaestudiantil.dto.request.RegistroDelegadoRequest;
import com.torneo.copaestudiantil.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse registrarDelegado(RegistroDelegadoRequest request);
}
