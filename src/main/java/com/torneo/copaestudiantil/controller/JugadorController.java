package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.request.search.JugadorSearchRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import com.torneo.copaestudiantil.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "06. Jugadores", description = "Gestión de jugadores del torneo")
@RestController
@RequestMapping("/api/admin/jugadores")
@RequiredArgsConstructor
public class JugadorController {

    private final JugadorService jugadorService;

    @Operation(
            summary = "Buscar jugadores",
            description = """
            Filtros opcionales: activo, nombres (prefijo), apellidoPaterno (prefijo),
            tipoDocumento, numeroDocumento (exacto), nacionalidad (exacto),
            anioNacimientoDesde, anioNacimientoHasta, tieneFoto, edicionId, equipoId.
            Usa `nextCursor` del response para paginar.
            """
    )
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CursorData<JugadorResponse>>> search(
            @RequestBody JugadorSearchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.search(request), CodigoNegocio.S_JUG_200_002));
    }

    @Operation(summary = "Obtener jugador por ID", description = "Retorna el jugador activo o inactivo")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JugadorResponse>> buscarPorId(
            @Parameter(description = "ID del jugador") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.obtenerPorId(id), CodigoNegocio.S_JUG_200_001));
    }

    @Operation(summary = "Buscar jugador por número de documento")
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<ApiResponse<JugadorResponse>> buscarPorDocumento(
            @Parameter(description = "DNI, pasaporte o carnet de extranjería")
            @PathVariable String numeroDocumento) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.obtenerPorDocumento(numeroDocumento),
                        CodigoNegocio.S_JUG_200_001));
    }

    @Operation(summary = "Crear jugador", description = "El número de documento debe ser único")
    @PostMapping
    public ResponseEntity<ApiResponse<JugadorResponse>> crear(
            @RequestBody JugadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(jugadorService.crear(request),
                        CodigoNegocio.S_JUG_201_001));
    }

    @Operation(summary = "Actualizar jugador", description = "Incluye `activo: true` para reactivar un jugador desactivado")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JugadorResponse>> actualizar(
            @PathVariable Long id,
            @RequestBody JugadorRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.actualizar(id, request),
                        CodigoNegocio.S_JUG_200_003));
    }

    @Operation(summary = "Subir foto del jugador", description = "Formatos: JPG o PNG. Tamaño máximo: 5MB")
    @PutMapping("/{id}/imagen")
    public ResponseEntity<ApiResponse<JugadorResponse>> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ApiResponse.ok(jugadorService.subirImagen(id, file),
                        CodigoNegocio.S_JUG_200_003));
    }

    @Operation(summary = "Desactivar jugador", description = "Soft delete — el jugador no se elimina de la BD")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        jugadorService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_JUG_204_001));
    }
}
