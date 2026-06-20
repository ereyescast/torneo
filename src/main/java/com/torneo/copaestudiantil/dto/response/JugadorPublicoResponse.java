package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Jugador para la VISTA PÚBLICA (plantel del equipo).
 *
 * IMPORTANTE (Ley 29733 — datos de menores): NO expone número de documento,
 * fecha de nacimiento exacta ni otros datos personales sensibles. Solo lo
 * mínimo para identificar al jugador (nombre) más sus estadísticas DEPORTIVAS
 * acumuladas en la edición, que no son datos personales protegidos.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JugadorPublicoResponse {
    private Long id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;

    /** Posición en cancha (Arquero, Defensa, Mediocampista, Delantero). Dato deportivo. */
    private String posicion;

    /**
     * Foto del jugador. SOLO se llena si el jugador tiene consentimiento parental
     * registrado (Ley 29733). Si no hay consentimiento, va null y el front muestra iniciales.
     */
    private String profileImage;

    // ── Estadísticas deportivas acumuladas en la edición ──────────────────────
    @Builder.Default private Long goles       = 0L;
    @Builder.Default private Long asistencias = 0L;
    @Builder.Default private Long amarillas   = 0L;
    @Builder.Default private Long rojas       = 0L;

    /**
     * Partidos en los que el jugador registró alguna estadística.
     * No es un conteo exacto de presencias (el modelo actual solo guarda
     * estadística cuando el jugador aporta algo), pero da una referencia.
     */
    @Builder.Default private Long partidos    = 0L;
}
