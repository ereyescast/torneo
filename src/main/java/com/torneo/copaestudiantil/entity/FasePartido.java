package com.torneo.copaestudiantil.entity;

/**
 * Fases del torneo Estudiantil Cup Callao.
 *
 * Flujo típico:
 *   GRUPOS → CUARTOS_ORO / CUARTOS_PLATA → SEMIFINAL_ORO / SEMIFINAL_PLATA
 *          → TERCER_PUESTO_ORO / TERCER_PUESTO_PLATA → FINAL_ORO / FINAL_PLATA
 *
 * Para torneos sin fase Plata, solo se usan GRUPOS, SEMIFINAL_ORO y FINAL_ORO.
 */
public enum FasePartido {

    /** Fase de grupos (tabla de posiciones activa) */
    GRUPOS,

    /** Cuartos de final — rama campeón */
    CUARTOS_ORO,

    /** Cuartos de final — rama consolación */
    CUARTOS_PLATA,

    /** Semifinal — rama campeón */
    SEMIFINAL_ORO,

    /** Semifinal — rama consolación */
    SEMIFINAL_PLATA,

    /** Tercer puesto — rama campeón */
    TERCER_PUESTO_ORO,

    /** Tercer puesto — rama consolación */
    TERCER_PUESTO_PLATA,

    /** Gran final — campeón del torneo */
    FINAL_ORO,

    /** Final de consolación */
    FINAL_PLATA
}
