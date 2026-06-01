package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.PartidoRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.PagoService;
import com.torneo.copaestudiantil.service.PartidoService;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository       partidoRepository;
    private final EdicionTorneoRepository edicionRepository;
    private final CategoriaRepository     categoriaRepository;
    private final SedeRepository          sedeRepository;
    private final EquipoRepository        equipoRepository;
    private final GrupoRepository         grupoRepository;
    private final TablaPosicionService    tablaPosicionService;
    private final PagoService             pagoService;

    // ── Creación ──────────────────────────────────────────────────────────────

    @Override
    public PartidoResponse crear(PartidoRequest request) {
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
        Equipo local = equipoRepository.findById(request.getEquipoLocalId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo local no encontrado"));
        Equipo visitante = equipoRepository.findById(request.getEquipoVisitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo visitante no encontrado"));

        if (local.getId().equals(visitante.getId()))
            throw new BadRequestException("El equipo local y visitante no pueden ser el mismo");

        FasePartido fase = request.getFase() != null ? request.getFase() : FasePartido.GRUPOS;
        Grupo grupo = null;
        if (FasePartido.GRUPOS.equals(fase)) {
            if (request.getGrupoId() == null)
                throw new BadRequestException("grupoId es obligatorio para partidos de fase GRUPOS");
            grupo = grupoRepository.findById(request.getGrupoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        }

        Partido partido = Partido.builder()
                .organizadorId(request.getOrganizadorId())
                .edicion(edicion).categoria(categoria).sede(sede)
                .equipoLocal(local).equipoVisitante(visitante)
                .fechaHora(request.getFechaHora())
                .estado(request.getEstado() != null
                        ? request.getEstado() : EstadoPartido.PROGRAMADO)
                .fase(fase).grupo(grupo).activo(true)
                .build();

        return toResponse(partidoRepository.save(partido));
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PartidoResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listarPorEdicionYCategoria(Long edicionId, Long categoriaId) {
        return partidoRepository.findByEdicionIdAndCategoriaId(edicionId, categoriaId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listarPorFase(Long edicionId, Long categoriaId, FasePartido fase) {
        return partidoRepository.findByEdicionIdAndCategoriaIdAndFase(edicionId, categoriaId, fase)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listarPorGrupo(Long grupoId) {
        return partidoRepository.findByGrupoId(grupoId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listarPorEquipo(Long equipoId) {
        return partidoRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> historialPorEquipoYEdicion(Long equipoId, Long edicionId) {
        return partidoRepository.findHistorialPorEquipoYEdicion(equipoId, edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> historialCompleto(Long equipoId) {
        return partidoRepository.findHistorialCompleto(equipoId)
                .stream().map(this::toResponse).toList();
    }

    // ── Cambios de estado ─────────────────────────────────────────────────────

    @Override
    public PartidoResponse iniciar(Long id) {
        Partido partido = findById(id);
        if (!EstadoPartido.PROGRAMADO.equals(partido.getEstado()))
            throw new BadRequestException(
                    "Solo se puede iniciar un partido PROGRAMADO. Estado actual: "
                            + partido.getEstado());
        partido.setEstado(EstadoPartido.EN_JUEGO);
        return toResponse(partidoRepository.save(partido));
    }

    @Override
    public PartidoResponse suspender(Long id) {
        Partido partido = findById(id);
        if (EstadoPartido.FINALIZADO.equals(partido.getEstado())
                || EstadoPartido.WO.equals(partido.getEstado()))
            throw new BadRequestException(
                    "No se puede suspender un partido ya finalizado o con WO");
        partido.setEstado(EstadoPartido.SUSPENDIDO);
        return toResponse(partidoRepository.save(partido));
    }

    @Override
    public PartidoResponse registrarResultado(Long id, Integer golesLocal, Integer golesVisitante) {
        Partido partido = findById(id);
        if (!EstadoPartido.EN_JUEGO.equals(partido.getEstado()))
            throw new BadRequestException(
                    "El partido debe estar EN_JUEGO para registrar resultado. Estado actual: "
                            + partido.getEstado());
        if (golesLocal < 0 || golesVisitante < 0)
            throw new BadRequestException("Los goles no pueden ser negativos");

        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado(EstadoPartido.FINALIZADO);

        Partido guardado = partidoRepository.save(partido);
        tablaPosicionService.actualizarTablaAlFinalizarPartido(guardado);
        return toResponse(guardado);
    }

    @Override
    public PartidoResponse registrarWo(Long id, Long equipoWoId) {
        Partido partido = findById(id);

        if (EstadoPartido.FINALIZADO.equals(partido.getEstado())
                || EstadoPartido.WO.equals(partido.getEstado()))
            throw new BadRequestException(
                    "No se puede aplicar WO a un partido ya finalizado o con WO previo");

        if (EstadoPartido.CANCELADO.equals(partido.getEstado()))
            throw new BadRequestException("No se puede aplicar WO a un partido cancelado");

        boolean esLocal     = partido.getEquipoLocal().getId().equals(equipoWoId);
        boolean esVisitante = partido.getEquipoVisitante().getId().equals(equipoWoId);

        if (!esLocal && !esVisitante)
            throw new BadRequestException("El equipoWoId no pertenece a este partido");

        if (esLocal) {
            partido.setGolesLocal(0);
            partido.setGolesVisitante(2);
        } else {
            partido.setGolesLocal(2);
            partido.setGolesVisitante(0);
        }

        partido.setEstado(EstadoPartido.WO);
        Partido guardado = partidoRepository.save(partido);
        tablaPosicionService.actualizarTablaAlFinalizarPartido(guardado);

        // Generar multa S/.50 automáticamente (Art. 16a)
        pagoService.generarMultaWo(guardado, equipoWoId);

        // ── Art. 16b — verificar si el equipo acumula 2 WOs ─────────────────
        long totalWos = partidoRepository.contarWosPorEquipoEnEdicion(
                equipoWoId, partido.getEdicion().getId());

        if (totalWos >= 2) {
            // Eliminar automáticamente al equipo del torneo
            Equipo equipoWo = esLocal
                    ? partido.getEquipoLocal()
                    : partido.getEquipoVisitante();
            equipoWo.setActivo(false);
            equipoRepository.save(equipoWo);

            // El response incluirá que el equipo fue eliminado
            // El organizador verá el equipo inactivo en el siguiente listado
        }

        return toResponse(guardado);
    }

    @Override
    public void cancelar(Long id) {
        Partido partido = findById(id);
        if (EstadoPartido.FINALIZADO.equals(partido.getEstado())
                || EstadoPartido.WO.equals(partido.getEstado()))
            throw new BadRequestException("No se puede cancelar un partido ya finalizado");
        partido.setActivo(false);
        partido.setEstado(EstadoPartido.CANCELADO);
        partidoRepository.save(partido);
    }

    // ── Privados ──────────────────────────────────────────────────────────────

    private Partido findById(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Partido no encontrado con id: " + id));
    }

    private EquipoResponse toEquipoResponse(Equipo e) {
        return EquipoResponse.builder()
                .id(e.getId()).organizadorId(e.getOrganizadorId())
                .nombre(e.getNombre()).logoUrl(e.getLogoUrl()).activo(e.getActivo())
                .edicion(EdicionTorneoResponse.builder()
                        .id(e.getEdicion().getId()).nombre(e.getEdicion().getNombre())
                        .fechaInicio(e.getEdicion().getFechaInicio())
                        .fechaFin(e.getEdicion().getFechaFin())
                        .activa(e.getEdicion().getActiva()).build())
                .categoria(CategoriaResponse.builder()
                        .id(e.getCategoria().getId())
                        .anioNacimiento(e.getCategoria().getAnioNacimiento())
                        .modalidad(e.getCategoria().getModalidad())
                        .nivel(e.getCategoria().getNivel())
                        .activa(e.getCategoria().getActiva()).build())
                .sede(SedeResponse.builder()
                        .id(e.getSede().getId()).nombre(e.getSede().getNombre())
                        .direccion(e.getSede().getDireccion())
                        .activa(e.getSede().getActiva()).build())
                .build();
    }

    private PartidoResponse toResponse(Partido p) {
        return PartidoResponse.builder()
                .id(p.getId()).organizadorId(p.getOrganizadorId())
                .edicion(EdicionTorneoResponse.builder()
                        .id(p.getEdicion().getId()).nombre(p.getEdicion().getNombre())
                        .fechaInicio(p.getEdicion().getFechaInicio())
                        .fechaFin(p.getEdicion().getFechaFin())
                        .activa(p.getEdicion().getActiva()).build())
                .categoria(CategoriaResponse.builder()
                        .id(p.getCategoria().getId())
                        .anioNacimiento(p.getCategoria().getAnioNacimiento())
                        .modalidad(p.getCategoria().getModalidad())
                        .nivel(p.getCategoria().getNivel())
                        .activa(p.getCategoria().getActiva()).build())
                .sede(SedeResponse.builder()
                        .id(p.getSede().getId()).nombre(p.getSede().getNombre())
                        .direccion(p.getSede().getDireccion())
                        .activa(p.getSede().getActiva()).build())
                .equipoLocal(toEquipoResponse(p.getEquipoLocal()))
                .equipoVisitante(toEquipoResponse(p.getEquipoVisitante()))
                .fechaHora(p.getFechaHora())
                .golesLocal(p.getGolesLocal())
                .golesVisitante(p.getGolesVisitante())
                .estado(p.getEstado()).fase(p.getFase())
                .grupoId(p.getGrupo() != null ? p.getGrupo().getId() : null)
                .cancha(p.getCancha())
                .fixtureId(p.getFixture() != null ? p.getFixture().getId() : null)
                .activo(p.getActivo())
                .build();
    }
}
