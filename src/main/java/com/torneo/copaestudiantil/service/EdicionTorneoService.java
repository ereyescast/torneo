package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;

import java.util.List;

public interface EdicionTorneoService {
    EdicionTorneoResponse crear(EdicionTorneoRequest request);
    EdicionTorneoResponse obtenerPorId(Long id);
    List<EdicionTorneoResponse> listarTodas();
    List<EdicionTorneoResponse> listarPorOrganizador(Long organizadorId);
    EdicionTorneoResponse actualizar(Long id, EdicionTorneoRequest request);
    void desactivar(Long id);
}
