package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.dto.response.ExisteEdicionResponse;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "03. Ediciones", description = "Ediciones del torneo (ej: Copa Callao Enero 2026)")
@RestController
@RequestMapping("/api/admin/ediciones")
@RequiredArgsConstructor
public class EdicionTorneoController {

    private final EdicionTorneoService edicionService;

    @Operation(summary = "Buscar ediciones", description = "Filtra automáticamente por el organizador del token")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<EdicionTorneoResponse>>> search(
            @RequestBody EdicionSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.search(request), CodigoNegocio.S_EDI_200_002));
    }

    @Operation(
        summary = "Verificar si existe una edición",
        description = """
            El front llama este endpoint ANTES de guardar para evitar duplicados.
            Verifica por nombre + fecha de inicio dentro del organizador del token.
            Si existe=true, retorna los datos de la edición existente.
            """
    )
    @GetMapping("/existe")
    public ResponseEntity<ApiResponse<ExisteEdicionResponse>> verificarExistencia(
            @Parameter(description = "Nombre exacto de la edición") @RequestParam String nombre,
            @Parameter(description = "Fecha de inicio (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio) {
        // organizadorId se obtiene del token dentro del service (se pasa null aquí)
        return ResponseEntity.ok(
                ApiResponse.ok(
                        edicionService.verificarExistencia(null, nombre, fechaInicio),
                        CodigoNegocio.S_EDI_200_001));
    }

    @Operation(summary = "Obtener edición por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EdicionTorneoResponse>> buscarPorId(
            @Parameter(description = "ID de la edición") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(edicionService.obtenerPorId(id), CodigoNegocio.S_EDI_200_001));
    }

    @Operation(summary = "Crear edición",
            description = "El organizador sale del token. Bloquea duplicado exacto (nombre + fecha de inicio)")
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
