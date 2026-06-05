package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.util.SlugUtils;
import com.torneo.copaestudiantil.dto.request.LoginRequest;
import com.torneo.copaestudiantil.dto.request.RegisterRequest;
import com.torneo.copaestudiantil.dto.response.AuthResponse;
import com.torneo.copaestudiantil.entity.Organizador;
import com.torneo.copaestudiantil.entity.RolUsuario;
import com.torneo.copaestudiantil.entity.Usuario;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.repository.OrganizadorRepository;
import com.torneo.copaestudiantil.repository.UsuarioRepository;
import com.torneo.copaestudiantil.security.JwtUtil;
import com.torneo.copaestudiantil.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final OrganizadorRepository organizadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        String token = jwtUtil.generarToken(usuario);
        return construirResponse(token, usuario);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException(
                    "Ya existe un usuario con el email: " + request.getEmail());

        if (organizadorRepository.existsByNombre(request.getNombreOrganizador()))
            throw new BadRequestException(
                    "Ya existe un organizador llamado '" + request.getNombreOrganizador() + "'.");

        // Generar slug único para la URL pública
        String slug = generarSlugUnico(request.getNombreOrganizador());

        Organizador organizador = Organizador.builder()
                .nombre(request.getNombreOrganizador())
                .codigoPublico(slug)
                .email(request.getEmail())
                .activo(true)
                .build();
        organizador = organizadorRepository.save(organizador);

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(RolUsuario.ORGANIZADOR)
                .organizadorId(organizador.getId())
                .activo(true)
                .build();
        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuario);
        return construirResponse(token, usuario, organizador);
    }

    /**
     * Genera un slug único. Si "copa-kids" ya existe, prueba "copa-kids-2", etc.
     */
    private String generarSlugUnico(String nombre) {
        String base = SlugUtils.generar(nombre);
        String slug = base;
        int contador = 2;
        while (organizadorRepository.existsByCodigoPublico(slug)) {
            slug = base + "-" + contador;
            contador++;
        }
        return slug;
    }

    private AuthResponse construirResponse(String token, Usuario usuario) {
        Organizador org = usuario.getOrganizadorId() != null
                ? organizadorRepository.findById(usuario.getOrganizadorId()).orElse(null)
                : null;
        return construirResponse(token, usuario, org);
    }

    private AuthResponse construirResponse(String token, Usuario usuario, Organizador org) {
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .organizadorId(usuario.getOrganizadorId())
                .nombreOrganizador(org != null ? org.getNombre() : null)
                .codigoPublico(org != null ? org.getCodigoPublico() : null)
                .build();
    }
}
