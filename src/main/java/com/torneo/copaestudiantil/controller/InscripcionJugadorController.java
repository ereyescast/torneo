package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.request.InscripcionJugadorRequest;
import com.torneo.copaestudiantil.dto.response.InscripcionJugadorResponse;
import com.torneo.copaestudiantil.service.InscripcionJugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "09. Inscripciones", description = "Inscripción de jugadores a equipos por edición")
@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionJugadorController {

    private final InscripcionJugadorService inscripcionService;

    @Operation(summary = "Listar jugadores inscritos de un equipo")
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<ApiResponse<List<InscripcionJugadorResponse>>> listarPorEquipo(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId) {
        return ResponseEntity.ok(
                ApiResponse.ok(inscripcionService.listarPorEquipo(equipoId),
                        CodigoNegocio.S_INS_201_001));
    }

    @Operation(summary = "Listar inscripciones de una edición")
    @GetMapping("/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<InscripcionJugadorResponse>>> listarPorEdicion(
            @Parameter(description = "ID de la edición") @PathVariable Long edicionId) {
        return ResponseEntity.ok(
                ApiResponse.ok(inscripcionService.listarPorEdicion(edicionId),
                        CodigoNegocio.S_INS_201_001));
    }

    @Operation(
            summary = "Inscribir jugador",
            description = """
            Reglas de inscripción:
            - Un jugador no puede estar inscrito en dos equipos de la misma edición (Art. 22)
            - El año de nacimiento del jugador debe corresponder a la categoría (Art. III)
            - El equipo no puede superar el máximo de jugadores por equipo (Art. 11)
            """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<InscripcionJugadorResponse>> inscribir(
            @RequestBody InscripcionJugadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(inscripcionService.inscribir(request),
                        CodigoNegocio.S_INS_201_001));
    }

    @Operation(summary = "Desinscribir jugador", description = "Soft delete de la inscripción")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desinscribir(
            @Parameter(description = "ID de la inscripción") @PathVariable Long id) {
        inscripcionService.desinscribir(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_INS_204_001));
    }
}
