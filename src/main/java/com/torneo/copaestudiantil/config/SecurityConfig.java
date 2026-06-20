package com.torneo.copaestudiantil.config;

import com.torneo.copaestudiantil.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── Autenticación (login/register) ──
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── Documentación y consola ──
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // ── VISTA PÚBLICA (padres/público, sin login) ──
                        // Todo bajo /api/public/** es de solo lectura y abierto.
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()

                        // ── PANEL ADMIN ──
                        // El DELEGADO solo puede gestionar jugadores e inscripciones (de su equipo).
                        .requestMatchers("/api/admin/jugadores/**", "/api/admin/inscripciones/**")
                            .hasAnyRole("ADMIN", "ORGANIZADOR", "DELEGADO")
                        // Solo el admin de plataforma genera códigos de organizador.
                        .requestMatchers("/api/admin/codigos-organizador/**")
                            .hasRole("ADMIN")
                        // El resto del panel es exclusivo de organizador/admin.
                        .requestMatchers("/api/admin/**")
                            .hasAnyRole("ADMIN", "ORGANIZADOR")

                        // ── Cualquier otra ruta requiere autenticación ──
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
