package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.mapper.TablaPosicionMapper;
import com.torneo.copaestudiantil.repository.TablaPosicionRepository;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TablaPosicionServiceImpl implements TablaPosicionService {

    private final TablaPosicionRepository tablaRepository;
    private final TablaPosicionMapper tablaMapper;

    /**
     * Inicializa la fila del equipo en la tabla de posiciones de su grupo.
     * Llamar al inscribir un equipo en un grupo.
     */
    @Override
    @Transactional
    public void inicializarEquipo(Equipo equipo, Grupo grupo) {
        TablaPosicion tabla = TablaPosicion.builder()
                .organizadorId(equipo.getOrganizadorId())
                .equipo(equipo)
                .edicion(equipo.getEdicion())
                .categoria(equipo.getCategoria())
                .grupo(grupo)
                .partidosJugados(0)
                .partidosGanados(0)
                .partidosEmpatados(0)
                .partidosPerdidos(0)
                .golesFavor(0)
                .golesContra(0)
                .diferenciaGol(0)
                .puntos(0)
                .build();

        tablaRepository.save(tabla);
    }

    /**
     * Actualiza la tabla de posiciones al finalizar un partido.
     * Solo procesa partidos de fase GRUPOS; en fases eliminatorias no hay tabla.
     */
    @Override
    @Transactional
    public void actualizarTablaAlFinalizarPartido(Partido partido) {

        // Solo FINALIZADO y WO actualizan la tabla
        if (!EstadoPartido.FINALIZADO.equals(partido.getEstado())
                && !EstadoPartido.WO.equals(partido.getEstado())) {
            return;
        }

        // Partidos eliminatorios no mueven tabla de posiciones
        if (!FasePartido.GRUPOS.equals(partido.getFase())) {
            return;
        }

        if (partido.getGrupo() == null) {
            throw new BadRequestException(
                    "El partido está en fase GRUPOS pero no tiene grupo asignado (id=" + partido.getId() + ")");
        }

        Long grupoId = partido.getGrupo().getId();
        Long edicionId = partido.getEdicion().getId();
        Long categoriaId = partido.getCategoria().getId();

        TablaPosicion local = tablaRepository
                .findByEquipoIdAndEdicionIdAndCategoriaIdAndGrupoId(
                        partido.getEquipoLocal().getId(), edicionId, categoriaId, grupoId)
                .orElseThrow(() -> new BadRequestException(
                        "Tabla no encontrada para equipo local en grupo " + grupoId));

        TablaPosicion visitante = tablaRepository
                .findByEquipoIdAndEdicionIdAndCategoriaIdAndGrupoId(
                        partido.getEquipoVisitante().getId(), edicionId, categoriaId, grupoId)
                .orElseThrow(() -> new BadRequestException(
                        "Tabla no encontrada para equipo visitante en grupo " + grupoId));

        int golesLocal = partido.getGolesLocal();
        int golesVisitante = partido.getGolesVisitante();

        local.setPartidosJugados(local.getPartidosJugados() + 1);
        visitante.setPartidosJugados(visitante.getPartidosJugados() + 1);

        local.setGolesFavor(local.getGolesFavor() + golesLocal);
        local.setGolesContra(local.getGolesContra() + golesVisitante);

        visitante.setGolesFavor(visitante.getGolesFavor() + golesVisitante);
        visitante.setGolesContra(visitante.getGolesContra() + golesLocal);

        if (golesLocal > golesVisitante) {
            local.setPartidosGanados(local.getPartidosGanados() + 1);
            local.setPuntos(local.getPuntos() + 3);
            visitante.setPartidosPerdidos(visitante.getPartidosPerdidos() + 1);
        } else if (golesLocal < golesVisitante) {
            visitante.setPartidosGanados(visitante.getPartidosGanados() + 1);
            visitante.setPuntos(visitante.getPuntos() + 3);
            local.setPartidosPerdidos(local.getPartidosPerdidos() + 1);
        } else {
            // Empate: en la Copa Kids hay tanda de penales, pero los puntos
            // se computan igual (1 pto cada uno). El ganador de penales
            // se registra en otra lógica si fuera necesario.
            local.setPartidosEmpatados(local.getPartidosEmpatados() + 1);
            visitante.setPartidosEmpatados(visitante.getPartidosEmpatados() + 1);
            local.setPuntos(local.getPuntos() + 1);
            visitante.setPuntos(visitante.getPuntos() + 1);
        }

        local.setDiferenciaGol(local.getGolesFavor() - local.getGolesContra());
        visitante.setDiferenciaGol(visitante.getGolesFavor() - visitante.getGolesContra());

        tablaRepository.save(local);
        tablaRepository.save(visitante);
    }

    /** Tabla de un grupo específico, ordenada por puntos/diferencia de gol/goles a favor */
    @Override
    public List<TablaPosicionResponse> obtenerTablaPorGrupo(Long grupoId) {
        return tablaRepository
                .findByGrupoIdOrderByPuntosDescDiferenciaGolDescGolesFavorDesc(grupoId)
                .stream()
                .map(tablaMapper::toResponse)
                .toList();
    }

    /** Tabla global de edición+categoría (útil si no hay fase de grupos) */
    @Override
    public List<TablaPosicionResponse> obtenerTabla(Long edicionId, Long categoriaId) {
        return tablaRepository
                .findByEdicionIdAndCategoriaIdOrderByPuntosDescDiferenciaGolDescGolesFavorDesc(
                        edicionId, categoriaId)
                .stream()
                .map(tablaMapper::toResponse)
                .toList();
    }
}