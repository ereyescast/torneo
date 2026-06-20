package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.util.SlugUtils;
import com.torneo.copaestudiantil.dto.request.LoginRequest;
import com.torneo.copaestudiantil.dto.request.RegisterRequest;
import com.torneo.copaestudiantil.dto.request.RegistroDelegadoRequest;
import com.torneo.copaestudiantil.dto.response.AuthResponse;
import com.torneo.copaestudiantil.entity.Delegado;
import com.torneo.copaestudiantil.entity.EstadoDelegado;
import com.torneo.copaestudiantil.entity.Organizador;
import com.torneo.copaestudiantil.entity.RolUsuario;
import com.torneo.copaestudiantil.entity.Usuario;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.repository.DelegadoRepository;
import com.torneo.copaestudiantil.repository.CodigoOrganizadorRepository;
import com.torneo.copaestudiantil.entity.CodigoOrganizador;
import com.torneo.copaestudiantil.entity.EstadoCodigoOrganizador;
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
    private final DelegadoRepository delegadoRepository;
    private final CodigoOrganizadorRepository codigoOrganizadorRepository;
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
        // ── Código de invitación (un solo uso) ───────────────────────────────
        CodigoOrganizador codigo = codigoOrganizadorRepository
                .findByCodigo(request.getCodigo().trim())
                .orElseThrow(() -> new BadRequestException("Código de invitación inválido."));
        if (codigo.getEstado() == EstadoCodigoOrganizador.USADO)
            throw new BadRequestException("Este código de invitación ya fue utilizado.");

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
                // Nombre normalizado para búsqueda sin tildes (lógica en el service)
                .nombreBusqueda(SlugUtils.normalizarBusqueda(request.getNombreOrganizador()))
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

        // ── Quemar el código (un solo uso) ───────────────────────────────────
        codigo.setEstado(EstadoCodigoOrganizador.USADO);
        codigo.setOrganizadorId(organizador.getId());
        codigo.setEmailUsado(request.getEmail());
        codigo.setFechaUso(java.time.LocalDateTime.now());
        codigoOrganizadorRepository.save(codigo);

        String token = jwtUtil.generarToken(usuario);
        return construirResponse(token, usuario, organizador);
    }

    @Override
    public AuthResponse registrarDelegado(RegistroDelegadoRequest request) {
        // 1) Validar código
        Delegado delegado = delegadoRepository.findByCodigoInvitacion(request.getCodigo().trim())
                .orElseThrow(() -> new BadRequestException("Código de invitación inválido."));

        if (delegado.getEstado() == EstadoDelegado.ACTIVO || delegado.getUsuarioId() != null)
            throw new BadRequestException("Este código ya fue usado por un delegado.");

        if (Boolean.FALSE.equals(delegado.getActivo()))
            throw new BadRequestException("Esta invitación fue desactivada.");

        // 2) Validar email libre
        if (usuarioRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Ya existe un usuario con el email: " + request.getEmail());

        // 3) Crear el Usuario con rol DELEGADO, atado a su equipo y organizador
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nombre(request.getNombres() + " " + request.getApellidosPaterno())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(RolUsuario.DELEGADO)
                .organizadorId(delegado.getOrganizadorId())
                .equipoId(delegado.getEquipoId())
                .activo(true)
                .build());

        // 4) Completar y activar el delegado
        delegado.setNombres(request.getNombres());
        delegado.setApellidosPaterno(request.getApellidosPaterno());
        delegado.setApellidosMaterno(request.getApellidosMaterno());
        delegado.setEmail(request.getEmail());
        delegado.setUsuarioId(usuario.getId());
        delegado.setEstado(EstadoDelegado.ACTIVO);
        delegadoRepository.save(delegado);

        String token = jwtUtil.generarToken(usuario);
        return construirResponse(token, usuario);
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
                .equipoId(usuario.getEquipoId())
                .nombreOrganizador(org != null ? org.getNombre() : null)
                .codigoPublico(org != null ? org.getCodigoPublico() : null)
                .build();
    }
}
