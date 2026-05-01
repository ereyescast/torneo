package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.EquipoRequest;
import com.torneo.copaestudiantil.dto.response.EquipoResponse;
import com.torneo.copaestudiantil.service.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping
    public ResponseEntity<List<EquipoResponse>> listarTodos() {
        return ResponseEntity.ok(equipoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.obtenerPorId(id));
    }

    @GetMapping("/edicion/{edicionId}/categoria/{categoriaId}")
    public ResponseEntity<List<EquipoResponse>> listarPorEdicionYCategoria(
            @PathVariable Long edicionId,
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(equipoService.listarPorEdicionYCategoria(edicionId, categoriaId));
    }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@RequestBody EquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody EquipoRequest request) {
        return ResponseEntity.ok(equipoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        equipoService.desactivar(id);
    }
}
