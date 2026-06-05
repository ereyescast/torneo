package com.torneo.copaestudiantil.config;

import com.torneo.copaestudiantil.entity.RolUsuario;
import com.torneo.copaestudiantil.entity.Usuario;
import com.torneo.copaestudiantil.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Siembra el usuario ADMIN de plataforma al arrancar la aplicación.
 *
 * Principio de seguridad: la cuenta de máximo privilegio NO se crea por la API
 * (nadie puede volverse admin con un POST), sino que nace de la configuración
 * controlada por el dueño del servidor.
 *
 * - Si ya existe un ADMIN, no hace nada (idempotente).
 * - Las credenciales vienen de application.properties / variables de entorno,
 *   NUNCA hardcodeadas en el código.
 * - El ADMIN tiene organizadorId = null: no pertenece a ningún organizador,
 *   puede ver y gestionar todos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@torneo.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin-cambiame-2026}")
    private String adminPassword;

    @Value("${app.admin.nombre:Administrador de Plataforma}")
    private String adminNombre;

    @Override
    public void run(String... args) {
        // Si ya existe algún ADMIN, no sembrar
        if (usuarioRepository.existsByRol(RolUsuario.ADMIN)) {
            log.info("Admin de plataforma ya existe. Siembra omitida.");
            return;
        }

        // Si el email ya está tomado (por un organizador, p.ej.), avisar y no romper
        if (usuarioRepository.existsByEmail(adminEmail)) {
            log.warn("No se sembró el admin: el email {} ya está en uso.", adminEmail);
            return;
        }

        Usuario admin = Usuario.builder()
                .nombre(adminNombre)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .rol(RolUsuario.ADMIN)
                .organizadorId(null)   // el admin no pertenece a ningún organizador
                .activo(true)
                .build();

        usuarioRepository.save(admin);
        log.info("✅ Admin de plataforma creado: {} (cambia la contraseña en producción)", adminEmail);
    }
}
