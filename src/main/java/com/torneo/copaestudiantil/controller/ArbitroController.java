package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.service.ArbitroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arbitros")
@RequiredArgsConstructor
public class ArbitroController {

    private final ArbitroService arbitroService;

    @PostMapping
    public ResponseEntity<ArbitroResponse> crear(
            @RequestParam Long organizadorId,
            @RequestBody ArbitroRequest request
    ) {
        return ResponseEntity.ok(arbitroService.crear(organizadorId, request));
    }

    @GetMapping
    public ResponseEntity<List<ArbitroResponse>> listar(
            @RequestParam Long organizadorId
    ) {
        return ResponseEntity.ok(arbitroService.listarActivos(organizadorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArbitroResponse> actualizar(
            @RequestParam Long organizadorId,
            @PathVariable Long id,
            @RequestBody ArbitroRequest request
    ) {
        return ResponseEntity.ok(arbitroService.actualizar(organizadorId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(
            @RequestParam Long organizadorId,
            @PathVariable Long id
    ) {
        arbitroService.desactivar(organizadorId, id);
        return ResponseEntity.noContent().build();
    }
}