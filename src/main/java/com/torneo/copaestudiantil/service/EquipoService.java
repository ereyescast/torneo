package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EquipoRequest;
import com.torneo.copaestudiantil.dto.request.search.EquipoSearchRequest;
import com.torneo.copaestudiantil.dto.response.EquipoResponse;

public interface EquipoService {
    CursorData<EquipoResponse> search(EquipoSearchRequest request);
    EquipoResponse crear(EquipoRequest request);
    EquipoResponse obtenerPorId(Long id);
    EquipoResponse actualizar(Long id, EquipoRequest request);
    void desactivar(Long id);
}
