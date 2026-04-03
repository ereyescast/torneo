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

    @GetMapping
    public ResponseEntity<List<TablaPosicionResponse>> obtenerTabla(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId
    ) {

        return ResponseEntity.ok(
                tablaPosicionService.obtenerTabla(edicionId, categoriaId)
        );
    }
}