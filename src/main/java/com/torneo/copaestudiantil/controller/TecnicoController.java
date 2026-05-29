package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.request.search.TecnicoSearchRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import com.torneo.copaestudiantil.service.TecnicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoService tecnicoService;

    /**
     * POST /api/tecnicos/search
     * Búsqueda con filtros dinámicos y cursor.
     *
     * Body ejemplo:
     * {
     *   "activo": true,
     *   "nombres": "Roberto",
     *   "pagination": { "limit": 20, "nextCursor": null }
     * }
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<TecnicoResponse>>> search(
            @RequestBody TecnicoSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(tecnicoService.search(request), CodigoNegocio.S_TEC_200_002));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TecnicoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(tecnicoService.obtenerPorId(id), CodigoNegocio.S_TEC_200_001));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TecnicoResponse>> registrar(
            @Valid @RequestBody TecnicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(tecnicoService.registrar(request),
                        CodigoNegocio.S_TEC_201_001));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TecnicoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TecnicoRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(tecnicoService.actualizar(id, request),
                        CodigoNegocio.S_TEC_200_003));
    }

    @PutMapping("/{id}/imagen")
    public ResponseEntity<ApiResponse<TecnicoResponse>> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ApiResponse.ok(tecnicoService.subirImagen(id, file),
                        CodigoNegocio.S_TEC_200_003));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        tecnicoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_TEC_204_001));
    }
}
