package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<InscripcionJugadorResponse>>> listarPorEquipo(
            @PathVariable Long equipoId) {
        return ResponseEntity.ok(
                ApiResponse.ok(inscripcionService.listarPorEquipo(equipoId),
                        CodigoNegocio.S_INS_201_001));
    }

    @GetMapping("/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<InscripcionJugadorResponse>>> listarPorEdicion(
            @PathVariable Long edicionId) {
        return ResponseEntity.ok(
                ApiResponse.ok(inscripcionService.listarPorEdicion(edicionId),
                        CodigoNegocio.S_INS_201_001));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InscripcionJugadorResponse>> inscribir(
            @RequestBody InscripcionJugadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(inscripcionService.inscribir(request),
                        CodigoNegocio.S_INS_201_001));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desinscribir(@PathVariable Long id) {
        inscripcionService.desinscribir(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_INS_204_001));
    }
}
