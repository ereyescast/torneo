package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.EstadisticaJugadorResponse;
import com.torneo.copaestudiantil.service.EstadisticaJugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Estadísticas de jugadores. El controller solo recibe la petición y delega
 * en EstadisticaJugadorService — toda la lógica (incluido el Art. 23) vive
 * en el service.
 */
@Tag(name = "14. Estadísticas", description = "Goles, asistencias y tarjetas por jugador en cada partido")
@RestController
@RequestMapping("/api/admin/estadisticas")
@RequiredArgsConstructor
public class EstadisticaJugadorController {

    private final EstadisticaJugadorService estadisticaService;

    @Operation(summary = "Estadísticas de un partido")
    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorPartido(
            @PathVariable Long partidoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaService.listarPorPartido(partidoId), CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Estadísticas de un jugador en una edición")
    @GetMapping("/jugador/{jugadorId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorJugadorYEdicion(
            @PathVariable Long jugadorId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaService.listarPorJugadorYEdicion(jugadorId, edicionId),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Estadísticas de un equipo en una edición")
    @GetMapping("/equipo/{equipoId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorEquipoYEdicion(
            @PathVariable Long equipoId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaService.listarPorEquipoYEdicion(equipoId, edicionId),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(
        summary = "Registrar estadística",
        description = """
            Registra las estadísticas de un jugador en un partido.

            ART. 23 — Si se registra tarjeta roja (tarjetasRojas >= 1),
            el sistema crea automáticamente una suspensión para la siguiente fecha.
            ART. 23 — Las tarjetas amarillas NO acumulan entre partidos.

            Requiere el `numeroFecha` actual para calcular la suspensión.
            """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<EstadisticaJugadorResponse>> registrar(
            @Parameter(description = "ID del jugador") @RequestParam Long jugadorId,
            @Parameter(description = "ID del partido") @RequestParam Long partidoId,
            @RequestParam Long equipoId,
            @RequestParam Long edicionId,
            @Parameter(description = "Número de fecha actual del torneo (1-6). Necesario para calcular suspensión por tarjeta roja")
            @RequestParam Integer numeroFecha,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @Parameter(description = "1 roja = suspensión automática siguiente fecha (Art. 23)")
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados,
            @RequestParam(defaultValue = "false") Boolean titular) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                estadisticaService.registrar(jugadorId, partidoId, equipoId, edicionId,
                        numeroFecha, goles, asistencias, tarjetasAmarillas, tarjetasRojas,
                        minutosJugados, titular),
                CodigoNegocio.S_PAR_200_003));
    }

    @Operation(summary = "Actualizar estadística")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EstadisticaJugadorResponse>> actualizar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados) {

        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaService.actualizar(id, goles, asistencias,
                        tarjetasAmarillas, tarjetasRojas, minutosJugados),
                CodigoNegocio.S_PAR_200_003));
    }
}
