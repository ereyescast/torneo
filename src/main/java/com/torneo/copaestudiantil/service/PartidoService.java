package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.PartidoRequest;
import com.torneo.copaestudiantil.dto.response.PartidoResponse;
import com.torneo.copaestudiantil.entity.FasePartido;

import java.util.List;

public interface PartidoService {

    PartidoResponse crear(PartidoRequest request);
    PartidoResponse obtenerPorId(Long id);
    List<PartidoResponse> listarPorEdicionYCategoria(Long edicionId, Long categoriaId);
    List<PartidoResponse> listarPorFase(Long edicionId, Long categoriaId, FasePartido fase);
    List<PartidoResponse> listarPorGrupo(Long grupoId);
    List<PartidoResponse> listarPorEquipo(Long equipoId);
    PartidoResponse iniciar(Long id);
    PartidoResponse suspender(Long id);
    PartidoResponse registrarResultado(Long id, Integer golesLocal, Integer golesVisitante);
    PartidoResponse registrarWo(Long id, Long equipoWoId);
    void cancelar(Long id);
}
