package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.PublicService;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicServiceImpl implements PublicService {

    private final OrganizadorRepository   organizadorRepository;
    private final EdicionTorneoRepository edicionRepository;
    private final CategoriaRepository     categoriaRepository;
    private final PartidoRepository       partidoRepository;
    private final TablaPosicionService    tablaPosicionService;

    // ── Resolver slug → Organizador ───────────────────────────────────────────

    private Organizador resolverOrganizador(String codigoPublico) {
        return organizadorRepository.findByCodigoPublico(codigoPublico)
                .filter(Organizador::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un torneo público con el código: " + codigoPublico));
    }

    /**
     * Valida que una edición pertenezca al organizador del slug.
     * Evita que alguien adivine IDs de otro organizador en la URL pública.
     */
    private void validarEdicionDelOrganizador(Long edicionId, Long organizadorId) {
        EdicionTorneo edicion = edicionRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        if (!edicion.getOrganizadorId().equals(organizadorId))
            throw new ResourceNotFoundException("Edición no encontrada en este torneo");
    }

    // ── Métodos públicos ────────────────────────────────────────────────────

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
        return partidoRepository.findByEdicionIdAndCategoriaId(edicionId, categoriaId)
                .stream().map(this::toPartidoResponse).toList();
    }

    // ── Mapeo de Partido (resumen para vista pública) ──────────────────────────

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
