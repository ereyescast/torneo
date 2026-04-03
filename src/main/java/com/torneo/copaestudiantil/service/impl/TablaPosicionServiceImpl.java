package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.Partido;
import com.torneo.copaestudiantil.entity.TablaPosicion;
import com.torneo.copaestudiantil.entity.Equipo;
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

    @Override
    @Transactional
    public void inicializarEquipo(Equipo equipo) {

        TablaPosicion tabla = TablaPosicion.builder()
                .organizadorId(equipo.getOrganizadorId())
                .equipo(equipo)
                .edicion(equipo.getEdicion())
                .categoria(equipo.getCategoria())
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

    @Override
    @Transactional
    public void actualizarTablaAlFinalizarPartido(Partido partido) {

        if (!EstadoPartido.FINALIZADO.equals(partido.getEstado())) {
            return;
        }

        TablaPosicion local = tablaRepository
                .findByEquipoIdAndEdicionIdAndCategoriaId(
                        partido.getEquipoLocal().getId(),
                        partido.getEdicion().getId(),
                        partido.getCategoria().getId()
                )
                .orElseThrow(() -> new BadRequestException("Tabla no encontrada equipo local"));

        TablaPosicion visitante = tablaRepository
                .findByEquipoIdAndEdicionIdAndCategoriaId(
                        partido.getEquipoVisitante().getId(),
                        partido.getEdicion().getId(),
                        partido.getCategoria().getId()
                )
                .orElseThrow(() -> new BadRequestException("Tabla no encontrada equipo visitante"));

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

    @Override
    public List<TablaPosicionResponse> obtenerTabla(Long edicionId, Long categoriaId) {

        return tablaRepository
                .findByEdicionIdAndCategoriaIdOrderByPuntosDescDiferenciaGolDescGolesFavorDesc(
                        edicionId,
                        categoriaId
                )
                .stream()
                .map(tablaMapper::toResponse)
                .toList();
    }
}