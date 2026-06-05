package com.torneo.copaestudiantil.entity;

/**
 * Documentos válidos para acreditar identidad de un jugador.
 *
 * ART. 4 y ART. 21 — Copa Estudiantil Cup Callao:
 * "El DNI original, pasaporte actualizado o la C4 de Reniec y PTP
 *  son los documentos para acreditar la identificación de un jugador."
 *
 * Nota sobre C4_RENIEC:
 *   Es una constancia temporal de Reniec que se usa cuando el jugador
 *   no tiene su DNI físico disponible ese día. No es un documento
 *   permanente — se verifica en mesa el día del partido (Art. 26).
 *   La vigencia de 1 mes se controla físicamente por el presidente
 *   de mesa, no en el sistema.
 *
 * Nota sobre PTP:
 *   Permiso Temporal de Permanencia — para jugadores extranjeros
 *   en proceso de regularización migratoria (venezolanos, colombianos, etc.)
 */
public enum TipoDocumento {
    DNI,
    PASAPORTE,
    CARNET_EXTRANJERIA,
    C4_RENIEC,
    PTP
}
