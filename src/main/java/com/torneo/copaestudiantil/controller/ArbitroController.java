package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.request.search.ArbitroSearchRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.service.ArbitroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "08. Árbitros", description = "Árbitros del torneo por organizador")
@RestController
@RequestMapping("/api/organizadores/{organizadorId}/arbitros")
@RequiredArgsConstructor
public class ArbitroController {

    private final ArbitroService arbitroService;

    @Operation(summary = "Buscar árbitros", description = "Filtros: activo, nombre (prefijo), email (exacto)")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<ArbitroResponse>>> search(
            @Parameter(description = "ID del organizador") @PathVariable Long organizadorId,
            @RequestBody ArbitroSearchRequest request) {
        if (request == null) request = new ArbitroSearchRequest();
        request.setOrganizadorId(organizadorId);
        return ResponseEntity.ok(
                ApiResponse.ok(arbitroService.search(request), CodigoNegocio.S_ARB_200_002));
    }

    @Operation(summary = "Obtener árbitro por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArbitroResponse>> buscarPorId(
            @PathVariable Long organizadorId,
            @Parameter(description = "ID del árbitro") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(arbitroService.obtenerPorId(id), CodigoNegocio.S_ARB_200_001));
    }

    @Operation(summary = "Crear árbitro")
    @PostMapping
    public ResponseEntity<ApiResponse<ArbitroResponse>> crear(
            @PathVariable Long organizadorId,
            @RequestBody ArbitroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(arbitroService.crear(organizadorId, request),
                        CodigoNegocio.S_ARB_201_001));
    }

    @Operation(summary = "Actualizar árbitro")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArbitroResponse>> actualizar(
            @PathVariable Long organizadorId,
            @PathVariable Long id,
            @RequestBody ArbitroRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(arbitroService.actualizar(organizadorId, id, request),
                        CodigoNegocio.S_ARB_200_003));
    }

    @Operation(summary = "Desactivar árbitro", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long organizadorId,
            @PathVariable Long id) {
        arbitroService.desactivar(organizadorId, id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_ARB_204_001));
    }
}
