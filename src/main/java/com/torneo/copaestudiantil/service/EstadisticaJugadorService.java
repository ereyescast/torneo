package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.EstadisticaJugadorResponse;

import java.util.List;

/**
 * Lógica de negocio de las estadísticas de jugadores (goles, asistencias,
 * tarjetas) por partido. Incluye la regla del Art. 23 (suspensión automática
 * por tarjeta roja).
 *
 * Toda la lógica vive aquí; el controller solo delega.
 */
public interface EstadisticaJugadorService {

    List<EstadisticaJugadorResponse> listarPorPartido(Long partidoId);

    List<EstadisticaJugadorResponse> listarPorJugadorYEdicion(Long jugadorId, Long edicionId);

    List<EstadisticaJugadorResponse> listarPorEquipoYEdicion(Long equipoId, Long edicionId);

    /**
     * Registra la estadística de un jugador en un partido.
     * Art. 23: si tarjetasRojas >= 1, crea suspensión automática para la
     * siguiente fecha (usando numeroFecha).
     */
    EstadisticaJugadorResponse registrar(
            Long jugadorId, Long partidoId, Long equipoId, Long edicionId,
            Integer numeroFecha, Integer goles, Integer asistencias,
            Integer tarjetasAmarillas, Integer tarjetasRojas,
            Integer minutosJugados, Boolean titular);

    EstadisticaJugadorResponse actualizar(
            Long id, Integer goles, Integer asistencias,
            Integer tarjetasAmarillas, Integer tarjetasRojas, Integer minutosJugados);
}
