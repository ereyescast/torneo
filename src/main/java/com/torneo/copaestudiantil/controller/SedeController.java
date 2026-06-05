package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.request.search.SedeSearchRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;
import com.torneo.copaestudiantil.service.SedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02. Sedes", description = "Campos deportivos donde se juegan los partidos")
@RestController
@RequestMapping("/api/admin/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @Operation(summary = "Buscar sedes", description = "Filtros opcionales: activa, nombre (prefijo), organizadorId")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<SedeResponse>>> search(
            @RequestBody SedeSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(sedeService.search(request), CodigoNegocio.S_SED_200_002));
    }

    @Operation(summary = "Obtener sede por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SedeResponse>> buscarPorId(
            @Parameter(description = "ID de la sede") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(sedeService.obtenerPorId(id), CodigoNegocio.S_SED_200_001));
    }

    @Operation(summary = "Crear sede")
    @PostMapping
    public ResponseEntity<ApiResponse<SedeResponse>> crear(@RequestBody SedeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(sedeService.crear(request), CodigoNegocio.S_SED_201_001));
    }

    @Operation(summary = "Actualizar sede", description = "Incluye campo `activa` para reactivar")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SedeResponse>> actualizar(
            @PathVariable Long id, @RequestBody SedeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(sedeService.actualizar(id, request), CodigoNegocio.S_SED_200_003));
    }

    @Operation(summary = "Desactivar sede", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        sedeService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_SED_204_001));
    }
}
