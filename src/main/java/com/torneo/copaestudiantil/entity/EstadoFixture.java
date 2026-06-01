package com.torneo.copaestudiantil.entity;

/**
 * Estado del fixture (programación de una fecha del torneo).
 *
 * BORRADOR   → el organizador está configurando canchas y ajustando horarios
 * PUBLICADO  → fixture listo, se puede compartir PDF por WhatsApp
 * FINALIZADO → todos los partidos de esa fecha terminaron
 */
public enum EstadoFixture {
    BORRADOR,
    PUBLICADO,
    FINALIZADO
}
