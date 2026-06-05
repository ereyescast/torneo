package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "12. Tabla de Posiciones", description = "Posiciones ordenadas por puntos, diferencia de goles y goles a favor")
@RestController
@RequestMapping("/api/admin/tabla-posiciones")
@RequiredArgsConstructor
public class TablaPosicionController {

    private final TablaPosicionService tablaPosicionService;

    @Operation(summary = "Tabla de posiciones de un grupo")
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<ApiResponse<List<TablaPosicionResponse>>> obtenerPorGrupo(
            @PathVariable Long grupoId) {
        return ResponseEntity.ok(
                ApiResponse.ok(tablaPosicionService.obtenerTablaPorGrupo(grupoId),
                        CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Tabla general por edición y categoría", description = "Todos los grupos combinados")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TablaPosicionResponse>>> obtenerTabla(
            @RequestParam Long edicionId, @RequestParam Long categoriaId) {
        return ResponseEntity.ok(
                ApiResponse.ok(tablaPosicionService.obtenerTabla(edicionId, categoriaId),
                        CodigoNegocio.S_GRU_200_002));
    }
}
