package com.torneo.copaestudiantil.security;

import com.torneo.copaestudiantil.entity.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // ── Generación ───────────────────────────────────────────────────────────

    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Incluir organizadorId y rol en el token (multi-tenancy)
        if (userDetails instanceof Usuario usuario) {
            claims.put("organizadorId", usuario.getOrganizadorId());
            claims.put("rol", usuario.getRol().name());
            claims.put("usuarioId", usuario.getId());
        }

        return buildToken(claims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey())
                .compact();
    }

    // ── Extracción ───────────────────────────────────────────────────────────

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Extrae el organizadorId del token.
     * Devuelve null si el usuario no tiene organizador (caso ADMIN).
     */
    public Long extraerOrganizadorId(String token) {
        return extraerClaim(token, claims -> {
            Object value = claims.get("organizadorId");
            if (value == null) return null;
            return ((Number) value).longValue();
        });
    }

    public Long extraerUsuarioId(String token) {
        return extraerClaim(token, claims -> {
            Object value = claims.get("usuarioId");
            if (value == null) return null;
            return ((Number) value).longValue();
        });
    }

    public String extraerRol(String token) {
        return extraerClaim(token, claims -> (String) claims.get("rol"));
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extraerTodosClaims(token));
    }

    private Claims extraerTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Validación ───────────────────────────────────────────────────────────

    public boolean esTokenValido(String token, UserDetails userDetails) {
        try {
            String email = extraerEmail(token);
            return email.equals(userDetails.getUsername()) && !estaExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    // ── Clave ────────────────────────────────────────────────────────────────

    private SecretKey getSignKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
