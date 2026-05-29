package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
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

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<ApiResponse<List<TablaPosicionResponse>>> obtenerPorGrupo(
            @PathVariable Long grupoId) {
        return ResponseEntity.ok(
                ApiResponse.ok(tablaPosicionService.obtenerTablaPorGrupo(grupoId),
                        CodigoNegocio.S_GRU_200_002));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TablaPosicionResponse>>> obtenerTabla(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(
                ApiResponse.ok(tablaPosicionService.obtenerTabla(edicionId, categoriaId),
                        CodigoNegocio.S_GRU_200_002));
    }
}
