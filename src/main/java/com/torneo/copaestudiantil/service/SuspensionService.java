package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.SuspensionResponse;
import com.torneo.copaestudiantil.entity.EstadisticaJugador;

import java.util.List;

public interface SuspensionService {

    /**
     * Crea automáticamente una suspensión cuando se registra tarjeta roja.
     * Llamado desde EstadisticaJugadorController al registrar estadísticas.
     * ART. 23 — suspensión = 1 fecha.
     */
    void procesarTarjetaRoja(EstadisticaJugador estadistica, Integer fechaActual);

    /** Verifica si un jugador está suspendido en una fecha específica */
    boolean estasSuspendido(Long jugadorId, Long edicionId, Integer numeroFecha);

    /** Lista suspensiones activas de un jugador en una edición */
    List<SuspensionResponse> listarPorJugador(Long jugadorId, Long edicionId);

    /** Lista todas las suspensiones de una edición */
    List<SuspensionResponse> listarPorEdicion(Long edicionId);

    /** Levanta una suspensión manualmente (casos especiales del organizador) */
    SuspensionResponse levantar(Long suspensionId);
}
