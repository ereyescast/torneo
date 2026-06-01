package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "03. Ediciones", description = "Ediciones del torneo (ej: Copa Callao Enero 2026)")
@RestController
@RequestMapping("/api/ediciones")
@RequiredArgsConstructor
public class EdicionTorneoController {

    private final EdicionTorneoService edicionService;

    @Operation(summary = "Buscar ediciones", description = "Filtros: activa, nombre (prefijo), organizadorId, fechas")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<EdicionTorneoResponse>>> search(
            @RequestBody EdicionSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.search(request), CodigoNegocio.S_EDI_200_002));
    }

    @Operation(summary = "Obtener edición por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> buscarPorId(
            @Parameter(description = "ID de la edición") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.obtenerPorId(id), CodigoNegocio.S_EDI_200_001));
    }

    @Operation(summary = "Crear edición")
    @PostMapping
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> crear(
            @RequestBody EdicionTorneoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(edicionService.crear(request),
                        CodigoNegocio.S_EDI_201_001));
    }

    @Operation(summary = "Actualizar edición")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> actualizar(
            @PathVariable Long id, @RequestBody EdicionTorneoRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.actualizar(id, request),
                        CodigoNegocio.S_EDI_200_003));
    }

    @Operation(summary = "Desactivar edición", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        edicionService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_EDI_204_001));
    }
}
