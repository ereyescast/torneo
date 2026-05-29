package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EquipoRequest;
import com.torneo.copaestudiantil.dto.request.search.EquipoSearchRequest;
import com.torneo.copaestudiantil.dto.response.EquipoResponse;
import com.torneo.copaestudiantil.service.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<EquipoResponse>>> search(
            @RequestBody EquipoSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(equipoService.search(request), CodigoNegocio.S_EQU_200_002));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(equipoService.obtenerPorId(id), CodigoNegocio.S_EQU_200_001));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EquipoResponse>> crear(@RequestBody EquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(equipoService.crear(request),
                        CodigoNegocio.S_EQU_201_001));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipoResponse>> actualizar(
            @PathVariable Long id, @RequestBody EquipoRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(equipoService.actualizar(id, request),
                        CodigoNegocio.S_EQU_200_003));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        equipoService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_EQU_204_001));
    }
}
