package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.request.search.ArbitroSearchRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.service.ArbitroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizadores/{organizadorId}/arbitros")
@RequiredArgsConstructor
public class ArbitroController {

    private final ArbitroService arbitroService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<ArbitroResponse>>> search(
            @PathVariable Long organizadorId,
            @RequestBody ArbitroSearchRequest request) {
        if (request == null) request = new ArbitroSearchRequest();
        request.setOrganizadorId(organizadorId);
        return ResponseEntity.ok(
                ApiResponse.ok(arbitroService.search(request), CodigoNegocio.S_ARB_200_002));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArbitroResponse>> buscarPorId(
            @PathVariable Long organizadorId, @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(arbitroService.obtenerPorId(id), CodigoNegocio.S_ARB_200_001));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ArbitroResponse>> crear(
            @PathVariable Long organizadorId,
            @RequestBody ArbitroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(arbitroService.crear(organizadorId, request),
                        CodigoNegocio.S_ARB_201_001));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArbitroResponse>> actualizar(
            @PathVariable Long organizadorId,
            @PathVariable Long id,
            @RequestBody ArbitroRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(arbitroService.actualizar(organizadorId, id, request),
                        CodigoNegocio.S_ARB_200_003));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long organizadorId, @PathVariable Long id) {
        arbitroService.desactivar(organizadorId, id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_ARB_204_001));
    }
}
