package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.CategoriaRequest;
import com.torneo.copaestudiantil.dto.request.search.CategoriaSearchRequest;
import com.torneo.copaestudiantil.dto.response.CategoriaResponse;
import com.torneo.copaestudiantil.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "04. Categorías", description = "Categorías por año de nacimiento y modalidad (F7, F8, F9)")
@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Buscar categorías", description = "Filtros: activa, edicionId, anioNacimiento, nivel, modalidad")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<CategoriaResponse>>> search(
            @RequestBody CategoriaSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(categoriaService.search(request), CodigoNegocio.S_CAT_200_002));
    }

    @Operation(summary = "Obtener categoría por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> buscarPorId(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(categoriaService.obtenerPorId(id), CodigoNegocio.S_CAT_200_001));
    }

    @Operation(summary = "Crear categoría", description = "El año de nacimiento + nivel debe ser único por edición")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(
            @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(categoriaService.crear(request),
                        CodigoNegocio.S_CAT_201_001));
    }

    @Operation(summary = "Actualizar categoría")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable Long id, @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(categoriaService.actualizar(id, request),
                        CodigoNegocio.S_CAT_200_003));
    }

    @Operation(summary = "Desactivar categoría", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_CAT_204_001));
    }
}
