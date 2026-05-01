package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoRepository grupoRepository;
    private final GrupoEquipoRepository grupoEquipoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EquipoRepository equipoRepository;
    private final TablaPosicionService tablaPosicionService;

    // ─── Mapeos a DTO (sin retornar entidades crudas) ───────────────────────

    private GrupoResponse toGrupoResponse(Grupo g) {
        EdicionTorneo e = g.getEdicion();
        Categoria c = g.getCategoria();
        return GrupoResponse.builder()
                .id(g.getId())
                .organizadorId(g.getOrganizadorId())
                .nombre(g.getNombre())
                .activo(g.getActivo())
                .edicion(EdicionTorneoResponse.builder()
                        .id(e.getId()).nombre(e.getNombre())
                        .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                        .activa(e.getActiva()).build())
                .categoria(CategoriaResponse.builder()
                        .id(c.getId()).anioNacimiento(c.getAnioNacimiento())
                        .modalidad(c.getModalidad()).nivel(c.getNivel())
                        .activa(c.getActiva()).build())
                .build();
    }

    private GrupoEquipoResponse toGrupoEquipoResponse(GrupoEquipo ge) {
        Equipo eq = ge.getEquipo();
        return GrupoEquipoResponse.builder()
                .id(ge.getId())
                .activo(ge.getActivo())
                .grupo(toGrupoResponse(ge.getGrupo()))
                .equipo(EquipoResponse.builder()
                        .id(eq.getId()).nombre(eq.getNombre())
                        .organizadorId(eq.getOrganizadorId())
                        .logoUrl(eq.getLogoUrl()).activo(eq.getActivo()).build())
                .build();
    }

    // ─── Endpoints ──────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<GrupoResponse>> listar(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        List<GrupoResponse> result = grupoRepository
                .findByEdicionIdAndCategoriaIdAndActivoTrue(edicionId, categoriaId)
                .stream().map(this::toGrupoResponse).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> buscarPorId(@PathVariable Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        return ResponseEntity.ok(toGrupoResponse(grupo));
    }

    @PostMapping
    public ResponseEntity<GrupoResponse> crear(
            @RequestParam Long organizadorId,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId,
            @RequestParam String nombre) {

        EdicionTorneo edicion = edicionTorneoRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Grupo grupo = Grupo.builder()
                .organizadorId(organizadorId)
                .edicion(edicion)
                .categoria(categoria)
                .nombre(nombre)
                .activo(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toGrupoResponse(grupoRepository.save(grupo)));
    }

    /**
     * Agrega un equipo al grupo e inicializa su fila en la tabla de posiciones.
     * Art. 15 — grupos de mínimo 4 equipos.
     */
    @PostMapping("/{grupoId}/equipos/{equipoId}")
    public ResponseEntity<GrupoEquipoResponse> agregarEquipo(
            @PathVariable Long grupoId,
            @PathVariable Long equipoId) {

        if (grupoEquipoRepository.existsByGrupoIdAndEquipoId(grupoId, equipoId)) {
            throw new BadRequestException("El equipo ya pertenece a este grupo");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));

        GrupoEquipo grupoEquipo = GrupoEquipo.builder()
                .grupo(grupo)
                .equipo(equipo)
                .activo(true)
                .build();

        GrupoEquipo guardado = grupoEquipoRepository.save(grupoEquipo);
        tablaPosicionService.inicializarEquipo(equipo, grupo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toGrupoEquipoResponse(guardado));
    }

    @GetMapping("/{grupoId}/equipos")
    public ResponseEntity<List<GrupoEquipoResponse>> listarEquipos(@PathVariable Long grupoId) {
        List<GrupoEquipoResponse> result = grupoEquipoRepository.findByGrupoId(grupoId)
                .stream().map(this::toGrupoEquipoResponse).toList();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        grupo.setActivo(false);
        grupoRepository.save(grupo);
    }
}
