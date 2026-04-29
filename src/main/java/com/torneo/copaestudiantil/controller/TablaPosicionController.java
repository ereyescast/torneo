package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tabla-posiciones")
@RequiredArgsConstructor
public class TablaPosicionController {

    private final TablaPosicionService tablaPosicionService;

    /**
     * Tabla de posiciones de un grupo específico.
     * GET /api/tabla-posiciones/grupo/{grupoId}
     */
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<TablaPosicionResponse>> obtenerPorGrupo(
            @PathVariable Long grupoId
    ) {
        return ResponseEntity.ok(tablaPosicionService.obtenerTablaPorGrupo(grupoId));
    }

    /**
     * Tabla global de una edición+categoría (todos los grupos).
     * GET /api/tabla-posiciones?edicionId=1&categoriaId=2
     */
    @GetMapping
    public ResponseEntity<List<TablaPosicionResponse>> obtenerTabla(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId
    ) {
        return ResponseEntity.ok(tablaPosicionService.obtenerTabla(edicionId, categoriaId));
    }
}