package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;
import com.torneo.copaestudiantil.service.SedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @GetMapping
    public ResponseEntity<List<SedeResponse>> listar() {
        return ResponseEntity.ok(sedeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SedeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sedeService.obtenerPorId(id));
    }

    @GetMapping("/organizador/{organizadorId}")
    public ResponseEntity<List<SedeResponse>> listarPorOrganizador(@PathVariable Long organizadorId) {
        return ResponseEntity.ok(sedeService.listarPorOrganizador(organizadorId));
    }

    @PostMapping
    public ResponseEntity<SedeResponse> crear(@RequestBody SedeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sedeService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SedeResponse> actualizar(
            @PathVariable Long id,
            @RequestBody SedeRequest request) {
        return ResponseEntity.ok(sedeService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        sedeService.desactivar(id);
    }
}
