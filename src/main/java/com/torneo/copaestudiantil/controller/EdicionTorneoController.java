package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ediciones")
@RequiredArgsConstructor
public class EdicionTorneoController {

    private final EdicionTorneoRepository edicionTorneoRepository;

    private EdicionTorneoResponse toResponse(EdicionTorneo e) {
        return EdicionTorneoResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .activa(e.getActiva())
                .build();
    }

    @GetMapping
    public List<EdicionTorneoResponse> listar() {
        return edicionTorneoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EdicionTorneoResponse> buscarPorId(@PathVariable Long id) {
        return edicionTorneoRepository.findById(id)
                .map(e -> ResponseEntity.ok(toResponse(e)))
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
    }

    @GetMapping("/organizador/{organizadorId}")
    public List<EdicionTorneoResponse> listarPorOrganizador(@PathVariable Long organizadorId) {
        return edicionTorneoRepository.findByOrganizadorId(organizadorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<EdicionTorneoResponse> crear(@RequestBody EdicionTorneoRequest request) {
        EdicionTorneo edicion = EdicionTorneo.builder()
                .organizadorId(request.getOrganizadorId())
                .nombre(request.getNombre())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();
        return ResponseEntity.ok(toResponse(edicionTorneoRepository.save(edicion)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EdicionTorneoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody EdicionTorneoRequest request) {
        EdicionTorneo edicion = edicionTorneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        edicion.setNombre(request.getNombre());
        edicion.setFechaInicio(request.getFechaInicio());
        edicion.setFechaFin(request.getFechaFin());
        edicion.setOrganizadorId(request.getOrganizadorId());
        return ResponseEntity.ok(toResponse(edicionTorneoRepository.save(edicion)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        EdicionTorneo edicion = edicionTorneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        edicion.setActiva(false);
        edicionTorneoRepository.save(edicion);
        return ResponseEntity.ok().build();
    }
}