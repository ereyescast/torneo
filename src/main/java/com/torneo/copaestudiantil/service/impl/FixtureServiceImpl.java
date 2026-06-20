package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.ConfiguracionCanchaRequest;
import com.torneo.copaestudiantil.dto.request.FixtureRequest;
import com.torneo.copaestudiantil.dto.request.GenerarFixtureRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.FixtureService;
import com.torneo.copaestudiantil.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FixtureServiceImpl implements FixtureService {

    private final FixtureRepository           fixtureRepository;
    private final ConfiguracionCanchaRepository canchaRepository;
    private final PartidoRepository           partidoRepository;
    private final EdicionTorneoRepository     edicionRepository;
    private final CategoriaRepository         categoriaRepository;
    private final SedeRepository              sedeRepository;
    private final GrupoRepository             grupoRepository;
    private final GrupoEquipoRepository       grupoEquipoRepository;

    // ── CRUD básico ──────────────────────────────────────────────────────────

    @Override
    public FixtureResponse crear(FixtureRequest request) {
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        }

        Fixture fixture = Fixture.builder()
                .organizadorId(SecurityUtils.getOrganizadorIdActual())
                .edicion(edicion)
                .categoria(categoria)
                .sede(sede)
                .fechaTorneo(request.getFechaTorneo())
                .numeroFecha(request.getNumeroFecha())
                .estado(EstadoFixture.BORRADOR)
                .build();

        return toResponse(fixtureRepository.save(fixture));
    }

    @Override
    @Transactional(readOnly = true)
    public FixtureResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixtureResponse> listarPorEdicion(Long edicionId) {
        return fixtureRepository.findByEdicionId(edicionId)
                .stream().map(this::toResponse).toList();
    }

    // ── Configurar canchas ───────────────────────────────────────────────────

    @Override
    public FixtureResponse configurarCanchas(Long fixtureId,
                                              List<ConfiguracionCanchaRequest> canchas) {
        Fixture fixture = findById(fixtureId);

        if (EstadoFixture.PUBLICADO.equals(fixture.getEstado())
                || EstadoFixture.FINALIZADO.equals(fixture.getEstado()))
            throw new BadRequestException(
                    "No se puede modificar un fixture " + fixture.getEstado());

        if (canchas == null || canchas.isEmpty())
            throw new BadRequestException("Debe configurar al menos una cancha");

        // Eliminar configuración anterior
        canchaRepository.deleteByFixtureId(fixtureId);

        // Guardar nueva configuración
        List<ConfiguracionCancha> nuevasCanchas = canchas.stream()
                .map(req -> {
                    if (req.getHoraFin().isBefore(req.getHoraInicio())
                            || req.getHoraFin().equals(req.getHoraInicio()))
                        throw new BadRequestException(
                                "La hora de fin debe ser posterior a la hora de inicio en: "
                                        + req.getNombreCancha());

                    // Usar duración del request, o el default del enum
                    int duracion = req.getDuracionPartidoMin() != null
                            ? req.getDuracionPartidoMin()
                            : req.getModalidad().getDuracionPartidoMin();

                    return ConfiguracionCancha.builder()
                            .fixture(fixture)
                            .nombreCancha(req.getNombreCancha())
                            .modalidad(req.getModalidad())
                            .horaInicio(req.getHoraInicio())
                            .horaFin(req.getHoraFin())
                            .duracionPartidoMin(duracion)
                            .build();
                }).toList();

        canchaRepository.saveAll(nuevasCanchas);
        return toResponse(fixtureRepository.findById(fixtureId).orElseThrow());
    }

    // ── Generar partidos inteligente ─────────────────────────────────────────

    @Override
    public List<PartidoResponse> generarPartidos(Long fixtureId,
                                                  GenerarFixtureRequest request) {
        Fixture fixture = findById(fixtureId);

        if (!EstadoFixture.BORRADOR.equals(fixture.getEstado()))
            throw new BadRequestException(
                    "Solo se puede generar partidos en un fixture BORRADOR");

        // Verificar que hay canchas configuradas
        List<ConfiguracionCancha> canchas = canchaRepository.findByFixtureId(fixtureId);
        if (canchas.isEmpty())
            throw new BadRequestException(
                    "Configure las canchas antes de generar el fixture");

        // Verificar que no hay partidos ya generados
        List<Partido> existentes = partidoRepository.findByFixtureId(fixtureId);
        if (!existentes.isEmpty())
            throw new BadRequestException(
                    "Ya existen " + existentes.size() + " partidos para este fixture. "
                            + "Elimina el fixture y crea uno nuevo para regenerar.");

        // Obtener grupos a programar
        List<Grupo> grupos;
        if (request.getGrupoId() != null) {
            Grupo grupo = grupoRepository.findById(request.getGrupoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
            grupos = List.of(grupo);
        } else if (fixture.getCategoria() != null) {
            grupos = grupoRepository.findByEdicionIdAndCategoriaIdAndActivoTrue(
                    fixture.getEdicion().getId(), fixture.getCategoria().getId());
        } else {
            throw new BadRequestException(
                    "Especifica un grupoId o asigna una categoría al fixture");
        }

        if (grupos.isEmpty())
            throw new BadRequestException("No hay grupos activos para generar partidos");

        // Generar todos los enfrentamientos por grupo
        List<Partido> todosLosPartidos = new ArrayList<>();
        for (Grupo grupo : grupos) {
            List<GrupoEquipo> grupoEquipos = grupoEquipoRepository.findByGrupoId(grupo.getId());
            if (grupoEquipos.size() < 2) continue;

            List<Equipo> equipos = grupoEquipos.stream()
                    .map(GrupoEquipo::getEquipo).toList();

            // Todos contra todos
            for (int i = 0; i < equipos.size(); i++) {
                for (int j = i + 1; j < equipos.size(); j++) {
                    todosLosPartidos.add(Partido.builder()
                            .organizadorId(SecurityUtils.getOrganizadorIdActual())
                            .edicion(fixture.getEdicion())
                            .categoria(fixture.getCategoria() != null
                                    ? fixture.getCategoria() : grupo.getCategoria())
                            .sede(fixture.getSede())
                            .equipoLocal(equipos.get(i))
                            .equipoVisitante(equipos.get(j))
                            .estado(EstadoPartido.PROGRAMADO)
                            .fase(FasePartido.GRUPOS)
                            .grupo(grupo)
                            .fixture(fixture)
                            .activo(true)
                            .build());
                }
            }
        }

        if (todosLosPartidos.isEmpty())
            throw new BadRequestException(
                    "No se generaron partidos. Verifica que los grupos tengan al menos 2 equipos");

        // Distribuir partidos en canchas por modalidad
        distribuirEnCanchas(todosLosPartidos, canchas, fixture);

        List<Partido> guardados = partidoRepository.saveAll(todosLosPartidos);
        return guardados.stream().map(this::toPartidoResponse).toList();
    }

    /**
     * Distribuye los partidos en las canchas disponibles.
     *
     * Algoritmo:
     * - Agrupa las canchas por modalidad
     * - Para cada partido, encuentra la cancha correcta (por modalidad de la categoría)
     * - Asigna la hora siguiente disponible en esa cancha
     * - Si una cancha se llena, pasa a la siguiente cancha de la misma modalidad
     */
    private void distribuirEnCanchas(List<Partido> partidos,
                                      List<ConfiguracionCancha> canchas,
                                      Fixture fixture) {

        // Estado actual de cada cancha: hora del próximo partido disponible
        List<LocalTime[]> proximaHora = new ArrayList<>();
        for (ConfiguracionCancha cancha : canchas) {
            proximaHora.add(new LocalTime[]{cancha.getHoraInicio()});
        }

        for (Partido partido : partidos) {
            ModalidadJuego modalidad = partido.getCategoria().getModalidad();

            // Buscar la cancha con la hora más temprana disponible para esta modalidad
            ConfiguracionCancha mejorCancha = null;
            LocalTime mejorHora = null;
            int mejorIndice = -1;

            for (int i = 0; i < canchas.size(); i++) {
                ConfiguracionCancha cancha = canchas.get(i);
                if (!cancha.getModalidad().equals(modalidad)) continue;

                LocalTime horaActual = proximaHora.get(i)[0];
                LocalTime horaConPartido = horaActual
                        .plusMinutes(cancha.getDuracionPartidoMin());

                // Verificar que el partido cabe antes de que cierre la cancha
                if (!horaConPartido.isAfter(cancha.getHoraFin())) {
                    if (mejorHora == null || horaActual.isBefore(mejorHora)) {
                        mejorHora = horaActual;
                        mejorCancha = cancha;
                        mejorIndice = i;
                    }
                }
            }

            if (mejorCancha == null)
                throw new BadRequestException(
                        "No hay suficiente espacio en las canchas de "
                                + modalidad.name() + " para todos los partidos. "
                                + "Agrega más canchas o amplía el horario.");

            // Asignar cancha y hora al partido
            partido.setCancha(mejorCancha.getNombreCancha());
            partido.setFechaHora(LocalDateTime.of(fixture.getFechaTorneo(), mejorHora));

            // Actualizar la próxima hora disponible en esa cancha
            proximaHora.get(mejorIndice)[0] = mejorHora
                    .plusMinutes(mejorCancha.getDuracionPartidoMin());
        }
    }

    // ── Cambios de estado ────────────────────────────────────────────────────

    @Override
    public FixtureResponse publicar(Long fixtureId) {
        Fixture fixture = findById(fixtureId);
        if (!EstadoFixture.BORRADOR.equals(fixture.getEstado()))
            throw new BadRequestException("Solo se puede publicar un fixture en BORRADOR");

        List<Partido> partidos = partidoRepository.findByFixtureId(fixtureId);
        if (partidos.isEmpty())
            throw new BadRequestException(
                    "No hay partidos generados. Genera los partidos antes de publicar.");

        fixture.setEstado(EstadoFixture.PUBLICADO);
        return toResponse(fixtureRepository.save(fixture));
    }

    @Override
    public FixtureResponse finalizar(Long fixtureId) {
        Fixture fixture = findById(fixtureId);
        if (!EstadoFixture.PUBLICADO.equals(fixture.getEstado()))
            throw new BadRequestException("Solo se puede finalizar un fixture PUBLICADO");
        fixture.setEstado(EstadoFixture.FINALIZADO);
        return toResponse(fixtureRepository.save(fixture));
    }

    @Override
    public void eliminar(Long fixtureId) {
        Fixture fixture = findById(fixtureId);
        if (!EstadoFixture.BORRADOR.equals(fixture.getEstado()))
            throw new BadRequestException(
                    "Solo se puede eliminar un fixture en BORRADOR. "
                            + "Estado actual: " + fixture.getEstado());
        // Eliminar partidos asociados primero
        List<Partido> partidos = partidoRepository.findByFixtureId(fixtureId);
        partidoRepository.deleteAll(partidos);
        fixtureRepository.delete(fixture);
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private Fixture findById(Long id) {
        return fixtureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fixture no encontrado"));
    }

    private FixtureResponse toResponse(Fixture f) {
        List<ConfiguracionCanchaResponse> canchasResp = canchaRepository
                .findByFixtureId(f.getId()).stream()
                .map(c -> ConfiguracionCanchaResponse.builder()
                        .id(c.getId())
                        .nombreCancha(c.getNombreCancha())
                        .modalidad(c.getModalidad())
                        .horaInicio(c.getHoraInicio())
                        .horaFin(c.getHoraFin())
                        .duracionPartidoMin(c.getDuracionPartidoMin())
                        .capacidadPartidos(c.getCapacidadPartidos())
                        .build())
                .toList();

        int totalPartidos = partidoRepository.findByFixtureId(f.getId()).size();

        EdicionTorneoResponse edicionResp = EdicionTorneoResponse.builder()
                .id(f.getEdicion().getId()).nombre(f.getEdicion().getNombre())
                .fechaInicio(f.getEdicion().getFechaInicio())
                .fechaFin(f.getEdicion().getFechaFin())
                .activa(f.getEdicion().getActiva()).build();

        CategoriaResponse catResp = null;
        if (f.getCategoria() != null) {
            catResp = CategoriaResponse.builder()
                    .id(f.getCategoria().getId())
                    .anioNacimiento(f.getCategoria().getAnioNacimiento())
                    .modalidad(f.getCategoria().getModalidad())
                    .nivel(f.getCategoria().getNivel())
                    .activa(f.getCategoria().getActiva()).build();
        }

        SedeResponse sedeResp = SedeResponse.builder()
                .id(f.getSede().getId()).nombre(f.getSede().getNombre())
                .direccion(f.getSede().getDireccion())
                .activa(f.getSede().getActiva()).build();

        return FixtureResponse.builder()
                .id(f.getId()).organizadorId(f.getOrganizadorId())
                .edicion(edicionResp).categoria(catResp).sede(sedeResp)
                .fechaTorneo(f.getFechaTorneo()).numeroFecha(f.getNumeroFecha())
                .estado(f.getEstado()).canchas(canchasResp)
                .totalPartidosGenerados(totalPartidos)
                .build();
    }

    private PartidoResponse toPartidoResponse(Partido p) {
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
                .golesLocal(p.getGolesLocal()).golesVisitante(p.getGolesVisitante())
                .estado(p.getEstado()).fase(p.getFase())
                .grupoId(p.getGrupo() != null ? p.getGrupo().getId() : null)
                .cancha(p.getCancha())
                .fixtureId(p.getFixture() != null ? p.getFixture().getId() : null)
                .activo(p.getActivo())
                .build();
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
}
