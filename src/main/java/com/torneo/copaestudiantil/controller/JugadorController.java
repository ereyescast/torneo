package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import com.torneo.copaestudiantil.service.JugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
@RequiredArgsConstructor
public class JugadorController {

    private final JugadorService jugadorService;

    @GetMapping
    public ResponseEntity<List<JugadorResponse>> listar() {
        return ResponseEntity.ok(jugadorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JugadorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jugadorService.obtenerPorId(id));
    }

    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<JugadorResponse> buscarPorDocumento(
            @PathVariable String numeroDocumento) {
        return ResponseEntity.ok(jugadorService.obtenerPorDocumento(numeroDocumento));
    }

    @PostMapping
    public ResponseEntity<JugadorResponse> crear(@RequestBody JugadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jugadorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JugadorResponse> actualizar(
            @PathVariable Long id,
            @RequestBody JugadorRequest request) {
        return ResponseEntity.ok(jugadorService.actualizar(id, request));
    }

    // Endpoint de imagen que antes no existía (estaba en JugadorServiceImpl pero sin endpoint)
    @PutMapping("/{id}/imagen")
    public ResponseEntity<JugadorResponse> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(jugadorService.subirImagen(id, file));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        jugadorService.desactivar(id);
    }
}
