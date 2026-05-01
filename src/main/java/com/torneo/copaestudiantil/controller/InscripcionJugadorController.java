package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.InscripcionJugadorRequest;
import com.torneo.copaestudiantil.dto.response.InscripcionJugadorResponse;
import com.torneo.copaestudiantil.service.InscripcionJugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionJugadorController {

    private final InscripcionJugadorService inscripcionService;

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<InscripcionJugadorResponse>> listarPorEquipo(
            @PathVariable Long equipoId) {
        return ResponseEntity.ok(inscripcionService.listarPorEquipo(equipoId));
    }

    @GetMapping("/edicion/{edicionId}")
    public ResponseEntity<List<InscripcionJugadorResponse>> listarPorEdicion(
            @PathVariable Long edicionId) {
        return ResponseEntity.ok(inscripcionService.listarPorEdicion(edicionId));
    }

    @PostMapping
    public ResponseEntity<InscripcionJugadorResponse> inscribir(
            @RequestBody InscripcionJugadorRequest request) {
        // Las validaciones de Art. 22 (doble inscripción) y Art. 11 (cupo máximo)
        // y Art. III (edad por categoría) se ejecutan en el servicio
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.inscribir(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desinscribir(@PathVariable Long id) {
        inscripcionService.desinscribir(id);
    }
}
