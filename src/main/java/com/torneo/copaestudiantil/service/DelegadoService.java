package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.DelegadoResponse;

public interface DelegadoService {

    /**
     * Invita (o reusa la invitación de) el delegado de un equipo: genera/devuelve
     * el código que el organizador comparte. Solo el organizador dueño del equipo.
     */
    DelegadoResponse invitar(Long equipoId);

    /** Devuelve el delegado de un equipo (o null si no tiene). Para el panel admin. */
    DelegadoResponse obtenerPorEquipo(Long equipoId);
}
