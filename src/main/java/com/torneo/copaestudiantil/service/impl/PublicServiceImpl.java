package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.PublicService;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import com.torneo.copaestudiantil.specification.OrganizadorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicServiceImpl implements PublicService {

    /** Tamaño del Top N para los rankings (goleadores y tarjetas). */
    private static final int TOP_RANKING = 30;

    private final OrganizadorRepository        organizadorRepository;
    private final EdicionTorneoRepository      edicionRepository;
    private final CategoriaRepository          categoriaRepository;
    private final PartidoRepository            partidoRepository;
    private final TablaPosicionService         tablaPosicionService;
    private final EstadisticaJugadorRepository estadisticaRepository;
    private final SedeRepository               sedeRepository;
    private final InscripcionJugadorRepository inscripcionRepository;
    private final TecnicoEquipoEdicionRepository tecnicoAsignacionRepository;
    private final DelegadoRepository            delegadoRepository;

    // ── Helpers internos ─────────────────────────────────────────────────────

    private Organizador resolverOrganizador(String codigoPublico) {
        return organizadorRepository.findByCodigoPublico(codigoPublico)
                .filter(Organizador::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un torneo público con el código: " + codigoPublico));
    }

    private void validarEdicionDelOrganizador(Long edicionId, Long organizadorId) {
        EdicionTorneo edicion = edicionRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        if (!edicion.getOrganizadorId().equals(organizadorId))
            throw new ResourceNotFoundException("Edición no encontrada en este torneo");
    }

    // ── Métodos públicos ──────────────────────────────────────────────────────

    @Override
    public OrganizadorPublicoResponse obtenerOrganizador(String codigoPublico) {
        Organizador org = resolverOrganizador(codigoPublico);
        return OrganizadorPublicoResponse.builder()
                .id(org.getId())
                .nombre(org.getNombre())
                .codigoPublico(org.getCodigoPublico())
                .logoUrl(org.getLogoUrl())
                .build();
    }

    @Override
    public List<TorneoDirectorioResponse> listarTorneos(String q) {
        Specification<Organizador> spec = OrganizadorSpecification.directorioPublico(q);
        return organizadorRepository
                .findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"))
                .stream()
                .map(org -> TorneoDirectorioResponse.builder()
                        .id(org.getId())
                        .nombre(org.getNombre())
                        .codigoPublico(org.getCodigoPublico())
                        .logoUrl(org.getLogoUrl())
                        .direccion(org.getDireccion())
                        .build())
                .toList();
    }

    @Override
    public List<EdicionResumenResponse> listarEdiciones(String codigoPublico) {
        Organizador org = resolverOrganizador(codigoPublico);
        return edicionRepository.findByOrganizadorIdAndActiva(org.getId(), true)
                .stream()
                .map(e -> EdicionResumenResponse.builder()
                        .id(e.getId()).nombre(e.getNombre())
                        .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                        .activa(e.getActiva()).build())
                .toList();
    }

    @Override
    public List<SedePublicaResponse> listarSedes(String codigoPublico) {
        Organizador org = resolverOrganizador(codigoPublico);
        return sedeRepository.findByOrganizadorIdAndActiva(org.getId(), true)
                .stream()
                .map(s -> SedePublicaResponse.builder()
                        .id(s.getId())
                        .nombre(s.getNombre())
                        .direccion(s.getDireccion())
                        .build())
                .toList();
    }

    @Override
    public List<CategoriaResumenResponse> listarCategorias(String codigoPublico, Long edicionId) {
        Organizador org = resolverOrganizador(codigoPublico);
        validarEdicionDelOrganizador(edicionId, org.getId());
        return categoriaRepository.findByEdicionIdAndActivaTrue(edicionId)
                .stream()
                .map(c -> CategoriaResumenResponse.builder()
                        .id(c.getId()).anioNacimiento(c.getAnioNacimiento())
                        .nivel(c.getNivel()).modalidad(c.getModalidad())
                        .activa(c.getActiva()).build())
                .toList();
    }

    @Override
    public List<TablaPosicionResponse> obtenerTabla(String codigoPublico,
                                                     Long edicionId, Long categoriaId) {
        Organizador org = resolverOrganizador(codigoPublico);
        validarEdicionDelOrganizador(edicionId, org.getId());
        return tablaPosicionService.obtenerTabla(edicionId, categoriaId);
    }

    @Override
    public List<PartidoPublicoResponse> listarPartidos(String codigoPublico,
                                                        Long edicionId, Long categoriaId) {
        Organizador org = resolverOrganizador(codigoPublico);
        validarEdicionDelOrganizador(edicionId, org.getId());
        return partidoRepository.findFixtureConDetalles(edicionId, categoriaId)
                .stream().map(this::toPartidoResponse).toList();
    }

    @Override
    public List<GoleadorResponse> rankingGoleadores(String codigoPublico,
                                                     Long edicionId,
                                                     Long categoriaId,
                                                     String fase) {
        Organizador org = resolverOrganizador(codigoPublico);
        validarEdicionDelOrganizador(edicionId, org.getId());
        return estadisticaRepository
                .rankingGoleadores(org.getId(), edicionId, categoriaId, fase, PageRequest.of(0, TOP_RANKING))
                .stream()
                .map(row -> GoleadorResponse.builder()
                        .jugadorId(((Number) row[0]).longValue())
                        .nombres((String) row[1])
                        .apellidoPaterno((String) row[2])
                        .apellidoMaterno((String) row[3])
                        .equipoNombre((String) row[4])
                        .totalGoles(((Number) row[5]).longValue())
                        .totalAsistencias(((Number) row[6]).longValue())
                        .build())
                .toList();
    }

    @Override
    public List<TarjetaResponse> rankingTarjetas(String codigoPublico,
                                                  Long edicionId,
                                                  Long categoriaId,
                                                  String fase) {
        Organizador org = resolverOrganizador(codigoPublico);
        validarEdicionDelOrganizador(edicionId, org.getId());
        return estadisticaRepository
                .rankingTarjetas(org.getId(), edicionId, categoriaId, fase, PageRequest.of(0, TOP_RANKING))
                .stream()
                .map(row -> TarjetaResponse.builder()
                        .jugadorId(((Number) row[0]).longValue())
                        .nombres((String) row[1])
                        .apellidoPaterno((String) row[2])
                        .apellidoMaterno((String) row[3])
                        .equipoNombre((String) row[4])
                        .totalAmarillas(((Number) row[5]).longValue())
                        .totalRojas(((Number) row[6]).longValue())
                        .build())
                .toList();
    }

    @Override
    public PlantelResponse listarPlantel(String codigoPublico, Long equipoId) {
        Organizador org = resolverOrganizador(codigoPublico);

        // 1) Stats acumuladas del equipo → mapa jugadorId -> fila agregada
        Map<Long, Object[]> statsPorJugador = estadisticaRepository.statsPorEquipo(equipoId)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),  // jugadorId
                        row -> row));

        // 2) Padrón del equipo (solo de este organizador) + merge con stats — 1 sola query (JOIN FETCH)
        List<JugadorPublicoResponse> jugadores = inscripcionRepository
                .findPlantelPublico(equipoId, org.getId())
                .stream()
                .map(i -> {
                    var j   = i.getJugador();
                    var row = statsPorJugador.get(j.getId());
                    return JugadorPublicoResponse.builder()
                            .id(j.getId())
                            .nombres(j.getNombres())
                            .apellidoPaterno(j.getApellidoPaterno())
                            .apellidoMaterno(j.getApellidoMaterno())
                            .posicion(j.getPosicion() != null ? j.getPosicion().name() : null)
                            // Ley 29733: foto pública SOLO con consentimiento parental
                            .profileImage(Boolean.TRUE.equals(j.getConsentimientoFoto())
                                    ? j.getProfileImage() : null)
                            .goles(      row != null ? ((Number) row[1]).longValue() : 0L)
                            .asistencias(row != null ? ((Number) row[2]).longValue() : 0L)
                            .amarillas(  row != null ? ((Number) row[3]).longValue() : 0L)
                            .rojas(      row != null ? ((Number) row[4]).longValue() : 0L)
                            .partidos(   row != null ? ((Number) row[5]).longValue() : 0L)
                            .build();
                })
                .sorted(Comparator
                        .comparingLong(JugadorPublicoResponse::getGoles).reversed()
                        .thenComparing(Comparator.comparingLong(JugadorPublicoResponse::getAsistencias).reversed())
                        .thenComparing(JugadorPublicoResponse::getApellidoPaterno,
                                Comparator.nullsLast(String::compareTo)))
                .toList();

        // 3) Técnico activo del equipo (puede no existir)
        TecnicoPublicoResponse tecnico = tecnicoAsignacionRepository
                .findTecnicoActivoDeEquipo(equipoId)
                .map(a -> {
                    var t = a.getTecnico();
                    return TecnicoPublicoResponse.builder()
                            .id(t.getId())
                            .nombres(t.getNombres())
                            .apellidosPaterno(t.getApellidosPaterno())
                            .apellidosMaterno(t.getApellidosMaterno())
                            .profileImage(t.getProfileImage())
                            .build();
                })
                .orElse(null);

        // 4) Delegado del equipo (solo si ya se registró → estado ACTIVO con nombre)
        DelegadoPublicoResponse delegado = delegadoRepository.findByEquipoIdAndActivoTrue(equipoId)
                .filter(d -> d.getNombres() != null)
                .map(d -> DelegadoPublicoResponse.builder()
                        .nombres(d.getNombres())
                        .apellidosPaterno(d.getApellidosPaterno())
                        .apellidosMaterno(d.getApellidosMaterno())
                        .build())
                .orElse(null);

        return PlantelResponse.builder()
                .tecnico(tecnico)
                .delegado(delegado)
                .jugadores(jugadores)
                .build();
    }

    // ── Mapeo interno ─────────────────────────────────────────────────────────

    private PartidoPublicoResponse toPartidoResponse(Partido p) {
        return PartidoPublicoResponse.builder()
                .id(p.getId())
                .fechaHora(p.getFechaHora())
                .golesLocal(p.getGolesLocal())
                .golesVisitante(p.getGolesVisitante())
                .estado(p.getEstado())
                .fase(p.getFase())
                .cancha(p.getCancha())
                .grupoId(p.getGrupo() != null ? p.getGrupo().getId() : null)
                .grupoNombre(p.getGrupo() != null ? p.getGrupo().getNombre() : null)
                .sedeNombre(p.getSede() != null ? p.getSede().getNombre() : null)
                .equipoLocal(EquipoResumenResponse.builder()
                        .id(p.getEquipoLocal().getId())
                        .nombre(p.getEquipoLocal().getNombre())
                        .logoUrl(p.getEquipoLocal().getLogoUrl())
                        .build())
                .equipoVisitante(EquipoResumenResponse.builder()
                        .id(p.getEquipoVisitante().getId())
                        .nombre(p.getEquipoVisitante().getNombre())
                        .logoUrl(p.getEquipoVisitante().getLogoUrl())
                        .build())
                .build();
    }
}
