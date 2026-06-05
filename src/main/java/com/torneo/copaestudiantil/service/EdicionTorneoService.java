package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.dto.response.ExisteEdicionResponse;

import java.time.LocalDate;

public interface EdicionTorneoService {
    CursorData<EdicionTorneoResponse> search(EdicionSearchRequest request);
    EdicionTorneoResponse crear(EdicionTorneoRequest request);
    EdicionTorneoResponse obtenerPorId(Long id);
    EdicionTorneoResponse actualizar(Long id, EdicionTorneoRequest request);
    void desactivar(Long id);

    /**
     * Verifica si ya existe una edición con el mismo organizador + nombre + fechaInicio.
     * Usado por el front ANTES de guardar para evitar duplicados accidentales.
     */
    ExisteEdicionResponse verificarExistencia(Long organizadorId, String nombre,
                                              LocalDate fechaInicio);
}
