package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.EquipoRequest;
import com.torneo.copaestudiantil.dto.response.EquipoResponse;

import java.util.List;

public interface EquipoService {
    EquipoResponse crear(EquipoRequest request);
    EquipoResponse obtenerPorId(Long id);
    List<EquipoResponse> listarTodos();
    List<EquipoResponse> listarPorEdicionYCategoria(Long edicionId, Long categoriaId);
    EquipoResponse actualizar(Long id, EquipoRequest request);
    void desactivar(Long id);
}
