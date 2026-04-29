package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.CategoriaRequest;
import com.torneo.copaestudiantil.dto.response.CategoriaResponse;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.CategoriaRepository;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;

    private CategoriaResponse toResponse(Categoria c) {
        EdicionTorneoResponse edicionResponse = null;
        if (c.getEdicion() != null) {
            EdicionTorneo e = c.getEdicion();
            edicionResponse = EdicionTorneoResponse.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .fechaInicio(e.getFechaInicio())
                    .fechaFin(e.getFechaFin())
                    .activa(e.getActiva())
                    .build();
        }
        return CategoriaResponse.builder()
                .id(c.getId())
                .organizadorId(c.getOrganizadorId())
                .edicion(edicionResponse)
                .anioNacimiento(c.getAnioNacimiento())
                .nivel(c.getNivel())
                .modalidad(c.getModalidad())
                .maxJugadoresPorEquipo(c.getMaxJugadoresPorEquipo())
                .activa(c.getActiva())
                .build();
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(c -> ResponseEntity.ok(toResponse(c)))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@RequestBody CategoriaRequest request) {
        EdicionTorneo edicion = edicionTorneoRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        Categoria categoria = Categoria.builder()
                .organizadorId(request.getOrganizadorId())
                .edicion(edicion)
                .anioNacimiento(request.getAnioNacimiento())
                .nivel(request.getNivel())
                .modalidad(request.getModalidad())
                .maxJugadoresPorEquipo(request.getMaxJugadoresPorEquipo())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();

        return ResponseEntity.ok(toResponse(categoriaRepository.save(categoria)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable Long id,
            @RequestBody CategoriaRequest request) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        EdicionTorneo edicion = edicionTorneoRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        categoria.setOrganizadorId(request.getOrganizadorId());
        categoria.setEdicion(edicion);
        categoria.setAnioNacimiento(request.getAnioNacimiento());
        categoria.setNivel(request.getNivel());
        categoria.setModalidad(request.getModalidad());
        categoria.setMaxJugadoresPorEquipo(request.getMaxJugadoresPorEquipo());

        return ResponseEntity.ok(toResponse(categoriaRepository.save(categoria)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
        return ResponseEntity.ok().build();
    }
}