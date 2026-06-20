package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.GrupoEquipoResponse;
import com.torneo.copaestudiantil.dto.response.GrupoResponse;
import com.torneo.copaestudiantil.service.GrupoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Grupos de la fase de grupos. El controller solo recibe la petición y
 * delega en GrupoService — toda la lógica vive en el service.
 */
@Tag(name = "10. Grupos", description = "Grupos de la fase de grupos del torneo")
@RestController
@RequestMapping("/api/admin/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;

    @Operation(summary = "Listar grupos por edición y categoría")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GrupoResponse>>> listar(
            @RequestParam Long edicionId, @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                grupoService.listar(edicionId, categoriaId), CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Obtener grupo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoResponse>> buscarPorId(
            @Parameter(description = "ID del grupo") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                grupoService.buscarPorId(id), CodigoNegocio.S_GRU_200_001));
    }

    @Operation(summary = "Crear grupo", description = "Ej: Grupo A, Grupo B")
    @PostMapping
    public ResponseEntity<ApiResponse<GrupoResponse>> crear(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId,
            @RequestParam String nombre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                grupoService.crear(edicionId, categoriaId, nombre), CodigoNegocio.S_GRU_201_001));
    }

    @Operation(summary = "Agregar equipo al grupo",
            description = "También inicializa la fila del equipo en la tabla de posiciones")
    @PostMapping("/{grupoId}/equipos/{equipoId}")
    public ResponseEntity<ApiResponse<GrupoEquipoResponse>> agregarEquipo(
            @PathVariable Long grupoId, @PathVariable Long equipoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                grupoService.agregarEquipo(grupoId, equipoId), CodigoNegocio.S_GRU_201_002));
    }

    @Operation(summary = "Listar equipos de un grupo")
    @GetMapping("/{grupoId}/equipos")
    public ResponseEntity<ApiResponse<List<GrupoEquipoResponse>>> listarEquipos(
            @PathVariable Long grupoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                grupoService.listarEquipos(grupoId), CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Desactivar grupo", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        grupoService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_GRU_200_001));
    }
}
