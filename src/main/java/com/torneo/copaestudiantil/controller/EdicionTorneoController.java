package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ediciones")
@RequiredArgsConstructor
public class EdicionTorneoController {

    private final EdicionTorneoService edicionService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<EdicionTorneoResponse>>> search(
            @RequestBody EdicionSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.search(request), CodigoNegocio.S_EDI_200_002));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.obtenerPorId(id), CodigoNegocio.S_EDI_200_001));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> crear(
            @RequestBody EdicionTorneoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(edicionService.crear(request),
                        CodigoNegocio.S_EDI_201_001));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> actualizar(
            @PathVariable Long id, @RequestBody EdicionTorneoRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.actualizar(id, request),
                        CodigoNegocio.S_EDI_200_003));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        edicionService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_EDI_204_001));
    }
}
