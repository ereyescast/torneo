package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.EquipoRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoRepository equipoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SedeRepository sedeRepository;

    private EquipoResponse toResponse(Equipo e) {
        EdicionTorneoResponse edicionResponse = EdicionTorneoResponse.builder()
                .id(e.getEdicion().getId())
                .nombre(e.getEdicion().getNombre())
                .fechaInicio(e.getEdicion().getFechaInicio())
                .fechaFin(e.getEdicion().getFechaFin())
                .activa(e.getEdicion().getActiva())
                .build();

        CategoriaResponse categoriaResponse = CategoriaResponse.builder()
                .id(e.getCategoria().getId())
                .anioNacimiento(e.getCategoria().getAnioNacimiento())
                .modalidad(e.getCategoria().getModalidad())
                .nivel(e.getCategoria().getNivel())
                .activa(e.getCategoria().getActiva())
                .build();

        SedeResponse sedeResponse = SedeResponse.builder()
                .id(e.getSede().getId())
                .nombre(e.getSede().getNombre())
                .direccion(e.getSede().getDireccion())
                .activa(e.getSede().getActiva())
                .build();

        return EquipoResponse.builder()
                .id(e.getId())
                .organizadorId(e.getOrganizadorId())
                .edicion(edicionResponse)
                .categoria(categoriaResponse)
                .sede(sedeResponse)
                .nombre(e.getNombre())
                .logoUrl(e.getLogoUrl())
                .activo(e.getActivo())
                .build();
    }

    @GetMapping
    public List<EquipoResponse> listarTodos() {
        return equipoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponse> buscarPorId(@PathVariable Long id) {
        return equipoRepository.findById(id)
                .map(e -> ResponseEntity.ok(toResponse(e)))
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
    }

    @GetMapping("/edicion/{edicionId}/categoria/{categoriaId}")
    public List<EquipoResponse> listarPorEdicionYCategoria(
            @PathVariable Long edicionId,
            @PathVariable Long categoriaId) {
        return equipoRepository.findByEdicionIdAndCategoriaId(edicionId, categoriaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@RequestBody EquipoRequest request) {
        EdicionTorneo edicion = edicionTorneoRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edicion no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Equipo equipo = Equipo.builder()
                .organizadorId(request.getOrganizadorId())
                .edicion(edicion)
                .categoria(categoria)
                .sede(sede)
                .nombre(request.getNombre())
                .logoUrl(request.getLogoUrl())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        // La tabla de posiciones se inicializa al asignar el equipo a un grupo
        // POST /api/grupos/{grupoId}/equipos/{equipoId}
        return ResponseEntity.ok(toResponse(equipoRepository.save(equipo)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody EquipoRequest request) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionTorneoRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edicion no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        equipo.setNombre(request.getNombre());
        equipo.setLogoUrl(request.getLogoUrl());
        equipo.setEdicion(edicion);
        equipo.setCategoria(categoria);
        equipo.setSede(sede);

        return ResponseEntity.ok(toResponse(equipoRepository.save(equipo)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        equipo.setActivo(false);
        equipoRepository.save(equipo);
        return ResponseEntity.ok().build();
    }
}
