package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.request.search.ArbitroSearchRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;

public interface ArbitroService {
    CursorData<ArbitroResponse> search(ArbitroSearchRequest request);
    ArbitroResponse crear(ArbitroRequest request);
    ArbitroResponse obtenerPorId(Long id);
    ArbitroResponse actualizar(Long arbitroId, ArbitroRequest request);
    void desactivar(Long arbitroId);
}
