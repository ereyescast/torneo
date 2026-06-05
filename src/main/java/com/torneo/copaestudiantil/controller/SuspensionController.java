package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.SuspensionResponse;
import com.torneo.copaestudiantil.service.SuspensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "16. Suspensiones", description = "Control de jugadores suspendidos por tarjeta roja (Art. 23)")
@RestController
@RequestMapping("/api/admin/suspensiones")
@RequiredArgsConstructor
public class SuspensionController {

    private final SuspensionService suspensionService;

    @Operation(
        summary = "Listar suspensiones de una edición",
        description = "Muestra todos los jugadores suspendidos actualmente en la edición"
    )
    @GetMapping("/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<SuspensionResponse>>> listarPorEdicion(
            @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                suspensionService.listarPorEdicion(edicionId),
                CodigoNegocio.S_JUG_200_002));
    }

    @Operation(summary = "Listar suspensiones de un jugador en una edición")
    @GetMapping("/jugador/{jugadorId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<SuspensionResponse>>> listarPorJugador(
            @PathVariable Long jugadorId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                suspensionService.listarPorJugador(jugadorId, edicionId),
                CodigoNegocio.S_JUG_200_002));
    }

    @Operation(
        summary = "Verificar si un jugador está suspendido",
        description = "Consulta si un jugador tiene suspensión activa en una fecha específica del torneo"
    )
    @GetMapping("/verificar")
    public ResponseEntity<ApiResponse<Boolean>> verificar(
            @RequestParam Long jugadorId,
            @RequestParam Long edicionId,
            @RequestParam Integer numeroFecha) {
        boolean suspendido = suspensionService.estasSuspendido(jugadorId, edicionId, numeroFecha);
        return ResponseEntity.ok(ApiResponse.ok(suspendido, CodigoNegocio.S_JUG_200_001));
    }

    @Operation(
        summary = "Levantar suspensión",
        description = "El organizador puede levantar manualmente una suspensión en casos excepcionales (Art. 45)"
    )
    @PutMapping("/{id}/levantar")
    public ResponseEntity<ApiResponse<SuspensionResponse>> levantar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                suspensionService.levantar(id),
                CodigoNegocio.S_JUG_200_003));
    }
}
