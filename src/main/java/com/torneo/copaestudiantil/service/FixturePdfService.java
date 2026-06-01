package com.torneo.copaestudiantil.service;

/**
 * Genera el PDF del fixture para compartir por WhatsApp.
 *
 * Formato:
 * ┌─────────────────────────────────────────┐
 * │    ESTUDIANTIL CUP CALLAO               │
 * │    Copa Kids — Enero 2026               │
 * │    Fecha 1 — Sábado 17 de Enero         │
 * │    Campo Deportivo Sor Ana              │
 * ├──────────┬──────────┬───────────────────┤
 * │  HORA    │  CAMPO   │  PARTIDO          │
 * ├──────────┼──────────┼───────────────────┤
 * │ 08:00 am │ Campo 1  │ Escuela A vs B    │
 * │ 08:30 am │ Campo 1  │ Escuela C vs D    │
 * │ 01:00 pm │ Campo 7  │ Escuela E vs F    │
 * └──────────┴──────────┴───────────────────┘
 */
public interface FixturePdfService {

    /**
     * Genera el PDF del fixture.
     * @param fixtureId ID del fixture a generar
     * @return bytes del PDF listo para descargar
     */
    byte[] generarPdf(Long fixtureId);
}
