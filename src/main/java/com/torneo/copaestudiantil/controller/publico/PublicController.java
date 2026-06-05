package com.torneo.copaestudiantil.controller.publico;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VISTA PÚBLICA — para padres y público general.
 *
 * Sin login. Solo lectura. Todo se accede por el slug del torneo.
 * Ejemplo de uso desde el front (link compartido por WhatsApp):
 *   /api/public/copa-estudiantil-callao/ediciones
 *   /api/public/copa-estudiantil-callao/tabla?edicionId=1&categoriaId=2
 */
@Tag(name = "00. Público", description = "Vista pública del torneo (sin login) — fixture, tabla, resultados")
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

    @Operation(summary = "Info del torneo", description = "Datos públicos del organizador por su código")
    @GetMapping("/{codigoPublico}")
    public ResponseEntity<ApiResponse<OrganizadorPublicoResponse>> organizador(
            @Parameter(description = "Código público del torneo, ej: copa-estudiantil-callao")
            @PathVariable String codigoPublico) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.obtenerOrganizador(codigoPublico), CodigoNegocio.S_SED_200_001));
    }

    @Operation(summary = "Ediciones del torneo")
    @GetMapping("/{codigoPublico}/ediciones")
    public ResponseEntity<ApiResponse<List<EdicionResumenResponse>>> ediciones(
            @PathVariable String codigoPublico) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarEdiciones(codigoPublico), CodigoNegocio.S_EDI_200_002));
    }

    @Operation(summary = "Categorías de una edición")
    @GetMapping("/{codigoPublico}/categorias")
    public ResponseEntity<ApiResponse<List<CategoriaResumenResponse>>> categorias(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarCategorias(codigoPublico, edicionId),
                CodigoNegocio.S_CAT_200_002));
    }

    @Operation(summary = "Tabla de posiciones", description = "Tabla de una edición + categoría")
    @GetMapping("/{codigoPublico}/tabla")
    public ResponseEntity<ApiResponse<List<TablaPosicionResponse>>> tabla(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.obtenerTabla(codigoPublico, edicionId, categoriaId),
                CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Partidos / fixture", description = "Fixture y resultados de una edición + categoría")
    @GetMapping("/{codigoPublico}/partidos")
    public ResponseEntity<ApiResponse<List<PartidoPublicoResponse>>> partidos(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarPartidos(codigoPublico, edicionId, categoriaId),
                CodigoNegocio.S_PAR_200_002));
    }
}
