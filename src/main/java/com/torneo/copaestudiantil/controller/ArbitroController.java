package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.service.ArbitroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizadores/{organizadorId}/arbitros")
@RequiredArgsConstructor
public class ArbitroController {

    private final ArbitroService arbitroService;

    // CREATE
    @PostMapping
    public ResponseEntity<ArbitroResponse> crear(
            @PathVariable Long organizadorId,
            @RequestBody ArbitroRequest request) {

        return ResponseEntity.ok(
                arbitroService.crear(organizadorId, request)
        );
    }

    // READ
    @GetMapping
    public ResponseEntity<List<ArbitroResponse>> listar(
            @PathVariable Long organizadorId) {

        return ResponseEntity.ok(
                arbitroService.listarActivos(organizadorId)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ArbitroResponse> actualizar(
            @PathVariable Long organizadorId,
            @PathVariable Long id,
            @RequestBody ArbitroRequest request) {

        return ResponseEntity.ok(
                arbitroService.actualizar(organizadorId, id, request)
        );
    }

    // DELETE (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long organizadorId,
            @PathVariable Long id) {

        arbitroService.desactivar(organizadorId, id);
        return ResponseEntity.noContent().build();
    }
}