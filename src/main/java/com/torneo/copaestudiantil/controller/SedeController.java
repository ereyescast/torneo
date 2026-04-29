package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;
import com.torneo.copaestudiantil.entity.Sede;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeRepository sedeRepository;

    private SedeResponse toResponse(Sede s) {
        return SedeResponse.builder()
                .id(s.getId())
                .organizadorId(s.getOrganizadorId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .activa(s.getActiva())
                .build();
    }

    @GetMapping
    public List<SedeResponse> listar() {
        return sedeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SedeResponse> buscarPorId(@PathVariable Long id) {
        return sedeRepository.findById(id)
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
    }

    @GetMapping("/organizador/{organizadorId}")
    public List<SedeResponse> listarPorOrganizador(@PathVariable Long organizadorId) {
        return sedeRepository.findByOrganizadorId(organizadorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SedeResponse> crear(@RequestBody SedeRequest request) {
        Sede sede = Sede.builder()
                .organizadorId(request.getOrganizadorId())
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();
        return ResponseEntity.ok(toResponse(sedeRepository.save(sede)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SedeResponse> actualizar(
            @PathVariable Long id,
            @RequestBody SedeRequest request) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
        sede.setNombre(request.getNombre());
        sede.setDireccion(request.getDireccion());
        sede.setOrganizadorId(request.getOrganizadorId());
        return ResponseEntity.ok(toResponse(sedeRepository.save(sede)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
        sede.setActiva(false);
        sedeRepository.save(sede);
        return ResponseEntity.ok().build();
    }
}