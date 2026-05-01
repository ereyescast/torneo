package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;

import java.util.List;

public interface SedeService {
    SedeResponse crear(SedeRequest request);
    SedeResponse obtenerPorId(Long id);
    List<SedeResponse> listarTodas();
    List<SedeResponse> listarPorOrganizador(Long organizadorId);
    SedeResponse actualizar(Long id, SedeRequest request);
    void desactivar(Long id);
}
