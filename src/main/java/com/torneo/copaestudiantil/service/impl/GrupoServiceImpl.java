package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.GrupoService;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository grupoRepository;
    private final GrupoEquipoRepository grupoEquipoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EquipoRepository equipoRepository;
    private final TablaPosicionService tablaPosicionService;
    private final TablaPosicionRepository tablaPosicionRepository;

    // ── Operaciones ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<GrupoResponse> listar(Long edicionId, Long categoriaId) {
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        return grupoRepository
                .findByEdicionIdAndCategoriaIdAndActivoTrue(edicionId, categoriaId)
                .stream()
                .filter(g -> g.getOrganizadorId().equals(organizadorId))
                .map(this::toGrupoResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GrupoResponse buscarPorId(Long id) {
        Grupo grupo = obtenerGrupoValidado(id);
        return toGrupoResponse(grupo);
    }

    @Override
    public GrupoResponse crear(Long edicionId, Long categoriaId, String nombre) {
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();

        EdicionTorneo edicion = edicionTorneoRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        SecurityUtils.validarPertenencia(edicion.getOrganizadorId());

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        SecurityUtils.validarPertenencia(categoria.getOrganizadorId());

        Grupo grupo = Grupo.builder()
                .organizadorId(organizadorId)
                .edicion(edicion)
                .categoria(categoria)
                .nombre(nombre)
                .activo(true)
                .build();

        return toGrupoResponse(grupoRepository.save(grupo));
    }

    @Override
    public GrupoResponse actualizarNombre(Long id, String nombre) {
        Grupo grupo = obtenerGrupoValidado(id);
        grupo.setNombre(nombre);
        return toGrupoResponse(grupoRepository.save(grupo));
    }

    @Override
    public GrupoEquipoResponse agregarEquipo(Long grupoId, Long equipoId) {
        Grupo grupo = obtenerGrupoValidado(grupoId);

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        SecurityUtils.validarPertenencia(equipo.getOrganizadorId());

        // Asignación activa actual del equipo (puede estar en otro grupo)
        GrupoEquipo actual = grupoEquipoRepository
                .findFirstByEquipoIdAndActivoTrue(equipoId).orElse(null);
        if (actual != null && actual.getGrupo().getId().equals(grupoId))
            throw new BadRequestException("El equipo ya pertenece a este grupo");

        // Límite del grupo destino (configurable en la categoría; null = sin límite)
        Integer maxEquipos = grupo.getCategoria() != null
                ? grupo.getCategoria().getMaxEquiposPorGrupo() : null;
        if (maxEquipos != null) {
            long actuales = grupoEquipoRepository.countByGrupoIdAndActivoTrue(grupoId);
            if (actuales >= maxEquipos)
                throw new BadRequestException(
                        "El grupo ya alcanzó el máximo de " + maxEquipos + " equipos permitidos para esta categoría.");
        }

        // Mover: desactivar la asignación previa en otro grupo y limpiar su fila de tabla
        if (actual != null) {
            actual.setActivo(false);
            grupoEquipoRepository.save(actual);
            tablaPosicionRepository
                    .findByEquipoIdAndEdicionIdAndCategoriaIdAndGrupoId(
                            equipoId, equipo.getEdicion().getId(),
                            equipo.getCategoria().getId(), actual.getGrupo().getId())
                    .ifPresent(tablaPosicionRepository::delete);
        }

        // Reactivar la fila si el equipo ya estuvo en este grupo, o crear una nueva
        GrupoEquipo guardado = grupoEquipoRepository
                .findFirstByGrupoIdAndEquipoId(grupoId, equipoId)
                .map(ge -> { ge.setActivo(true); return grupoEquipoRepository.save(ge); })
                .orElseGet(() -> grupoEquipoRepository.save(
                        GrupoEquipo.builder().grupo(grupo).equipo(equipo).activo(true).build()));

        // Inicializa la fila de tabla solo si no existe para este grupo
        boolean yaTieneTabla = tablaPosicionRepository
                .findByEquipoIdAndEdicionIdAndCategoriaIdAndGrupoId(
                        equipoId, equipo.getEdicion().getId(),
                        equipo.getCategoria().getId(), grupoId)
                .isPresent();
        if (!yaTieneTabla) {
            tablaPosicionService.inicializarEquipo(equipo, grupo);
        }

        return toGrupoEquipoResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrupoEquipoResponse> listarEquipos(Long grupoId) {
        obtenerGrupoValidado(grupoId);
        return grupoEquipoRepository.findByGrupoId(grupoId)
                .stream()
                .map(this::toGrupoEquipoResponse)
                .toList();
    }

    @Override
    public void desactivar(Long id) {
        Grupo grupo = obtenerGrupoValidado(id);
        grupo.setActivo(false);
        grupoRepository.save(grupo);
    }

    // ── Helpers internos ───────────────────────────────────────────────────────

    /** Busca el grupo y valida que pertenezca al organizador actual. */
    private Grupo obtenerGrupoValidado(Long grupoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        SecurityUtils.validarPertenencia(grupo.getOrganizadorId());
        return grupo;
    }

    private GrupoResponse toGrupoResponse(Grupo g) {
        EdicionTorneo e = g.getEdicion();
        Categoria c = g.getCategoria();
        return GrupoResponse.builder()
                .id(g.getId()).organizadorId(g.getOrganizadorId())
                .nombre(g.getNombre()).activo(g.getActivo())
                .edicion(EdicionTorneoResponse.builder()
                        .id(e.getId()).organizadorId(e.getOrganizadorId()).nombre(e.getNombre())
                        .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                        .activa(e.getActiva()).build())
                .categoria(CategoriaResponse.builder()
                        .id(c.getId()).organizadorId(c.getOrganizadorId())
                        .anioNacimiento(c.getAnioNacimiento())
                        .modalidad(c.getModalidad()).nivel(c.getNivel())
                        .maxEquiposPorGrupo(c.getMaxEquiposPorGrupo())
                        .activa(c.getActiva()).build())
                .cantidadEquipos(grupoEquipoRepository.countByGrupoIdAndActivoTrue(g.getId()))
                .build();
    }

    private GrupoEquipoResponse toGrupoEquipoResponse(GrupoEquipo ge) {
        Equipo eq = ge.getEquipo();
        return GrupoEquipoResponse.builder()
                .id(ge.getId()).activo(ge.getActivo())
                .grupo(toGrupoResponse(ge.getGrupo()))
                .equipo(EquipoResponse.builder()
                        .id(eq.getId()).nombre(eq.getNombre())
                        .organizadorId(eq.getOrganizadorId())
                        .logoUrl(eq.getLogoUrl()).activo(eq.getActivo()).build())
                .build();
    }
}
