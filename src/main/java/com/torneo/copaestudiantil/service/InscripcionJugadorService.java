package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.InscripcionJugadorRequest;
import com.torneo.copaestudiantil.dto.response.InscripcionJugadorResponse;

import java.util.List;

public interface InscripcionJugadorService {
    InscripcionJugadorResponse inscribir(InscripcionJugadorRequest request);
    List<InscripcionJugadorResponse> listarPorEquipo(Long equipoId);
    List<InscripcionJugadorResponse> listarPorEdicion(Long edicionId);
    void desinscribir(Long id);
}
