package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.request.search.ArbitroSearchRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;

public interface ArbitroService {
    CursorData<ArbitroResponse> search(ArbitroSearchRequest request);
    ArbitroResponse crear(Long organizadorId, ArbitroRequest request);
    ArbitroResponse obtenerPorId(Long id);
    ArbitroResponse actualizar(Long organizadorId, Long arbitroId, ArbitroRequest request);
    void desactivar(Long organizadorId, Long arbitroId);
}
