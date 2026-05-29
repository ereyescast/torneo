package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.request.search.SedeSearchRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;

public interface SedeService {
    CursorData<SedeResponse> search(SedeSearchRequest request);
    SedeResponse crear(SedeRequest request);
    SedeResponse obtenerPorId(Long id);
    SedeResponse actualizar(Long id, SedeRequest request);
    void desactivar(Long id);
}
