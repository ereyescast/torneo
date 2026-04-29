package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.entity.Equipo;
import com.torneo.copaestudiantil.entity.Grupo;
import com.torneo.copaestudiantil.entity.Partido;

import java.util.List;

public interface TablaPosicionService {

    /** Inicializa la fila del equipo en la tabla de su grupo */
    void inicializarEquipo(Equipo equipo, Grupo grupo);

    /** Actualiza la tabla solo si el partido es de fase GRUPOS */
    void actualizarTablaAlFinalizarPartido(Partido partido);

    /** Tabla de posiciones de un grupo específico */
    List<TablaPosicionResponse> obtenerTablaPorGrupo(Long grupoId);

    /** Tabla global de edición+categoría (todos los grupos juntos) */
    List<TablaPosicionResponse> obtenerTabla(Long edicionId, Long categoriaId);
}