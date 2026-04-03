package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.entity.Equipo;
import com.torneo.copaestudiantil.entity.Partido;

import java.util.List;

public interface TablaPosicionService {

    void inicializarEquipo(Equipo equipo);

    void actualizarTablaAlFinalizarPartido(Partido partido);

    List<TablaPosicionResponse> obtenerTabla(Long edicionId, Long categoriaId);
}