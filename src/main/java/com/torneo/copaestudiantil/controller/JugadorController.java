package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.request.search.JugadorSearchRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import com.torneo.copaestudiantil.service.JugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/jugadores")
@RequiredArgsConstructor
public class JugadorController {

    private final JugadorService jugadorService;

    /**
     * POST /api/jugadores/search
     * Búsqueda con filtros dinámicos y paginación por cursor.
     *
     * Body ejemplo:
     * {
     *   "filters": { "activo": true, "nombres": "Carlos", "anioNacimientoDesde": 2018 },
     *   "pagination": { "limit": 20, "nextCursor": null }
     * }
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<JugadorResponse>>> search(
            @RequestBody JugadorSearchRequest request) {

        CursorData<JugadorResponse> data = jugadorService.search(request);

        return ResponseEntity.ok(ApiResponse.ok(data, CodigoNegocio.S_JUG_200_002));
    }

    /**
     * GET /api/jugadores/{id}
     * Devuelve siempre — activo o no — para que el frontend pueda editar.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JugadorResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.obtenerPorId(id), CodigoNegocio.S_JUG_200_001));
    }

    /**
     * GET /api/jugadores/documento/{numeroDocumento}
     */
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<ApiResponse<JugadorResponse>> buscarPorDocumento(
            @PathVariable String numeroDocumento) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.obtenerPorDocumento(numeroDocumento),
                        CodigoNegocio.S_JUG_200_001));
    }

    /**
     * POST /api/jugadores
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JugadorResponse>> crear(
            @RequestBody JugadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(jugadorService.crear(request),
                        CodigoNegocio.S_JUG_201_001));
    }

    /**
     * PUT /api/jugadores/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JugadorResponse>> actualizar(
            @PathVariable Long id,
            @RequestBody JugadorRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.actualizar(id, request),
                        CodigoNegocio.S_JUG_200_003));
    }

    /**
     * PUT /api/jugadores/{id}/imagen
     */
    @PutMapping("/{id}/imagen")
    public ResponseEntity<ApiResponse<JugadorResponse>> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.subirImagen(id, file),
                        CodigoNegocio.S_JUG_200_003));
    }

    /**
     * DELETE /api/jugadores/{id} — soft delete
     */
    @DeleteMapping("/{id}")

    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        jugadorService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_JUG_204_001));
    }
}
