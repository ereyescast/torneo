package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public List<Grupo> listar(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return grupoRepository.findByEdicionIdAndCategoriaIdAndActivoTrue(edicionId, categoriaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grupo> buscarPorId(@PathVariable Long id) {
        return grupoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Grupo> crear(
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

        return ResponseEntity.ok(grupoRepository.save(grupo));
    }

    /**
     * Agrega un equipo al grupo e inicializa su fila en la tabla de posiciones.
     */
    @PostMapping("/{grupoId}/equipos/{equipoId}")
    public ResponseEntity<GrupoEquipo> agregarEquipo(
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

        // Inicializar fila en la tabla de posiciones del grupo
        tablaPosicionService.inicializarEquipo(equipo, grupo);

        return ResponseEntity.ok(guardado);
    }

    @GetMapping("/{grupoId}/equipos")
    public List<GrupoEquipo> listarEquipos(@PathVariable Long grupoId) {
        return grupoEquipoRepository.findByGrupoId(grupoId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        grupo.setActivo(false);
        grupoRepository.save(grupo);
        return ResponseEntity.ok().build();
    }
}