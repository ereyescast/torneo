package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ediciones")
@RequiredArgsConstructor
public class EdicionTorneoController {

    private final EdicionTorneoService edicionService;

    @GetMapping
    public ResponseEntity<List<EdicionTorneoResponse>> listar() {
        return ResponseEntity.ok(edicionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EdicionTorneoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(edicionService.obtenerPorId(id));
    }

    @GetMapping("/organizador/{organizadorId}")
    public ResponseEntity<List<EdicionTorneoResponse>> listarPorOrganizador(
            @PathVariable Long organizadorId) {
        return ResponseEntity.ok(edicionService.listarPorOrganizador(organizadorId));
    }

    @PostMapping
    public ResponseEntity<EdicionTorneoResponse> crear(@RequestBody EdicionTorneoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(edicionService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EdicionTorneoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody EdicionTorneoRequest request) {
        return ResponseEntity.ok(edicionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        edicionService.desactivar(id);
    }
}
