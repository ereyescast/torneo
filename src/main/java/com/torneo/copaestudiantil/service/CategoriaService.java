package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.CategoriaRequest;
import com.torneo.copaestudiantil.dto.request.search.CategoriaSearchRequest;
import com.torneo.copaestudiantil.dto.response.CategoriaResponse;

public interface CategoriaService {
    CursorData<CategoriaResponse> search(CategoriaSearchRequest request);
    CategoriaResponse crear(CategoriaRequest request);
    CategoriaResponse obtenerPorId(Long id);
    CategoriaResponse actualizar(Long id, CategoriaRequest request);
    void desactivar(Long id);
}
