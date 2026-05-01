package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.CategoriaRequest;
import com.torneo.copaestudiantil.dto.response.CategoriaResponse;

import java.util.List;

public interface CategoriaService {
    CategoriaResponse crear(CategoriaRequest request);
    CategoriaResponse obtenerPorId(Long id);
    List<CategoriaResponse> listarTodas();
    List<CategoriaResponse> listarPorEdicion(Long edicionId);
    CategoriaResponse actualizar(Long id, CategoriaRequest request);
    void desactivar(Long id);
}
