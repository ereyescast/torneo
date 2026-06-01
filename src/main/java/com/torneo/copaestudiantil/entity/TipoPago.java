package com.torneo.copaestudiantil.entity;

/**
 * Tipos de pago según las bases del torneo.
 *
 * XIV — COSTOS:
 *   INSCRIPCION: F7=S/.350, F8=S/.400, F9=S/.500
 *   ARBITRAJE:   F7=S/.45,  F8=S/.60,  F9=S/.70 por partido
 *
 * ART. 16a:
 *   MULTA_WO: S/.50 por cada WO
 *
 * ART. 40:
 *   MULTA_RECLAMO: S/.50 por cada reclamo presentado
 */
public enum TipoPago {
    INSCRIPCION,     // pago único al inicio del torneo
    ARBITRAJE,       // por partido (lo paga el equipo ganador en WO — Art. 16e)
    MULTA_WO,        // S/.50 por incurrir en WO (Art. 16a)
    MULTA_RECLAMO    // S/.50 por presentar un reclamo (Art. 40)
}
