package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.DelegadoResponse;
import com.torneo.copaestudiantil.service.DelegadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Delegados", description = "Invitación de delegados por equipo (organizador)")
@RestController
@RequestMapping("/api/admin/equipos/{equipoId}/delegado")
@RequiredArgsConstructor
public class DelegadoController {

    private final DelegadoService delegadoService;

    @Operation(summary = "Generar/obtener código de invitación del delegado")
    @PostMapping
    public ResponseEntity<ApiResponse<DelegadoResponse>> invitar(@PathVariable Long equipoId) {
        return ResponseEntity.ok(
                ApiResponse.ok(delegadoService.invitar(equipoId), CodigoNegocio.S_DEL_200_001));
    }

    @Operation(summary = "Ver el delegado del equipo (o null)")
    @GetMapping
    public ResponseEntity<ApiResponse<DelegadoResponse>> obtener(@PathVariable Long equipoId) {
        return ResponseEntity.ok(
                ApiResponse.ok(delegadoService.obtenerPorEquipo(equipoId), CodigoNegocio.S_DEL_200_001));
    }
}
