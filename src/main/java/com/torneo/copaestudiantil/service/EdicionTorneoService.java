package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;

public interface EdicionTorneoService {
    CursorData<EdicionTorneoResponse> search(EdicionSearchRequest request);
    EdicionTorneoResponse crear(EdicionTorneoRequest request);
    EdicionTorneoResponse obtenerPorId(Long id);
    EdicionTorneoResponse actualizar(Long id, EdicionTorneoRequest request);
    void desactivar(Long id);
}
