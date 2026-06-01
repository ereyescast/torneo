package com.torneo.copaestudiantil.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Modalidad de juego con duración y costos según bases del torneo.
 *
 * ART. 27 — Copa Estudiantil Cup Callao:
 *   F7: 15×20 min + 5 min descanso = 40 min total
 *   F9: 15×20 min + 5 min descanso = 40 min total
 *
 * XIV — Costos:
 *   F7: S/.350 inscripción + S/.45 arbitraje
 *   F8: S/.400 inscripción + S/.60 arbitraje
 *   F9: S/.500 inscripción + S/.70 arbitraje
 *
 * La duración es configurable por organizador via ConfiguracionCancha.
 * Estos valores son los defaults para Copa Estudiantil.
 */
@Getter
@RequiredArgsConstructor
public enum ModalidadJuego {

    FUTBOL_7(40, 350, 45, 4),   // duracion, inscripcion, arbitraje, min jugadores
    FUTBOL_8(40, 400, 60, 4),
    FUTBOL_9(40, 500, 70, 5),
    FUTBOL_11(90, 0, 0, 7);     // configurable para otros organizadores

    /** Duración total del partido en minutos (default). */
    private final int duracionPartidoMin;

    /** Costo de inscripción en soles (según bases). */
    private final int costoInscripcionSoles;

    /** Costo de arbitraje por partido en soles (según bases). */
    private final int costoArbitrajeSoles;

    /** Mínimo de jugadores en cancha para no perder por WO (ART. 28). */
    private final int minimoJugadoresEnCancha;
}
