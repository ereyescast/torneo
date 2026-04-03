package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;

import java.util.List;

public interface ArbitroService {

    ArbitroResponse crear(Long organizadorId, ArbitroRequest request);

    List<ArbitroResponse> listarActivos(Long organizadorId);

    ArbitroResponse actualizar(Long organizadorId, Long arbitroId, ArbitroRequest request);

    void desactivar(Long organizadorId, Long arbitroId);
}