package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.EstadisticaJugadorService;
import com.torneo.copaestudiantil.service.SuspensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class    EstadisticaJugadorServiceImpl implements EstadisticaJugadorService {

    private final EstadisticaJugadorRepository estadisticaRepository;
    private final JugadorRepository            jugadorRepository;
    private final PartidoRepository            partidoRepository;
    private final EquipoRepository             equipoRepository;
    private final EdicionTorneoRepository      edicionTorneoRepository;
    private final SuspensionService            suspensionService;

    // ── Consultas ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<EstadisticaJugadorResponse> listarPorPartido(Long partidoId) {
        return estadisticaRepository.findByPartidoId(partidoId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadisticaJugadorResponse> listarPorJugadorYEdicion(Long jugadorId, Long edicionId) {
        return estadisticaRepository.findByJugadorIdAndEdicionId(jugadorId, edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadisticaJugadorResponse> listarPorEquipoYEdicion(Long equipoId, Long edicionId) {
        return estadisticaRepository.findByEquipoIdAndEdicionId(equipoId, edicionId)
                .stream().map(this::toResponse).toList();
    }

    // ── Registro (con Art. 23) ───────────────────────────────────────────────────

    @Override
    public EstadisticaJugadorResponse registrar(
            Long jugadorId, Long partidoId, Long equipoId, Long edicionId,
            Integer numeroFecha, Integer goles, Integer asistencias,
            Integer tarjetasAmarillas, Integer tarjetasRojas,
            Integer minutosJugados, Boolean titular) {

        if (estadisticaRepository.findByJugadorIdAndPartidoId(jugadorId, partidoId).isPresent())
            throw new BadRequestException(
                    "Ya existe estadística para este jugador en este partido");

        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo ed = edicionTorneoRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        EstadisticaJugador est = EstadisticaJugador.builder()
                .organizadorId(SecurityUtils.getOrganizadorIdActual())
                .jugador(jugador).partido(partido).equipo(equipo).edicion(ed)
                .goles(goles).asistencias(asistencias)
                .tarjetasAmarillas(tarjetasAmarillas).tarjetasRojas(tarjetasRojas)
                .minutosJugados(minutosJugados).titular(titular)
                .build();

        EstadisticaJugador guardada = estadisticaRepository.save(est);

        // ART. 23 — suspensión automática por tarjeta roja
        if (tarjetasRojas != null && tarjetasRojas > 0) {
            suspensionService.procesarTarjetaRoja(guardada, numeroFecha);
        }

        return toResponse(guardada);
    }

    @Override
    public EstadisticaJugadorResponse actualizar(
            Long id, Integer goles, Integer asistencias,
            Integer tarjetasAmarillas, Integer tarjetasRojas, Integer minutosJugados) {

        EstadisticaJugador est = estadisticaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estadística no encontrada"));
        est.setGoles(goles);
        est.setAsistencias(asistencias);
        est.setTarjetasAmarillas(tarjetasAmarillas);
        est.setTarjetasRojas(tarjetasRojas);
        est.setMinutosJugados(minutosJugados);
        return toResponse(estadisticaRepository.save(est));
    }

    // ── Mapeo ────────────────────────────────────────────────────────────────────

    private EstadisticaJugadorResponse toResponse(EstadisticaJugador est) {
        Jugador j = est.getJugador();
        Equipo eq = est.getEquipo();
        EdicionTorneo ed = est.getEdicion();
        return EstadisticaJugadorResponse.builder()
                .id(est.getId())
                .organizadorId(est.getOrganizadorId())
                .partidoId(est.getPartido().getId())
                .goles(est.getGoles())
                .asistencias(est.getAsistencias())
                .tarjetasAmarillas(est.getTarjetasAmarillas())
                .tarjetasRojas(est.getTarjetasRojas())
                .minutosJugados(est.getMinutosJugados())
                .titular(est.getTitular())
                .jugador(JugadorResponse.builder()
                        .id(j.getId()).nombres(j.getNombres())
                        .apellidoPaterno(j.getApellidoPaterno())
                        .apellidoMaterno(j.getApellidoMaterno())
                        .tipoDocumento(j.getTipoDocumento())
                        .numeroDocumento(j.getNumeroDocumento())
                        .fechaNacimiento(j.getFechaNacimiento())
                        .nacionalidad(j.getNacionalidad())
                        .activo(j.getActivo()).build())
                .equipo(EquipoResponse.builder()
                        .id(eq.getId()).nombre(eq.getNombre())
                        .organizadorId(eq.getOrganizadorId())
                        .activo(eq.getActivo()).build())
                .edicion(EdicionTorneoResponse.builder()
                        .id(ed.getId()).nombre(ed.getNombre())
                        .fechaInicio(ed.getFechaInicio()).fechaFin(ed.getFechaFin())
                        .activa(ed.getActiva()).build())
                .build();
    }
}
