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

@Tag(name = "00. Público", description = "Vista pública del torneo (sin login)")
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

    @Operation(summary = "Directorio de torneos")
    @GetMapping("/torneos")
    public ResponseEntity<ApiResponse<List<TorneoDirectorioResponse>>> torneos(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarTorneos(q), CodigoNegocio.S_ORG_200_002));
    }

    @Operation(summary = "Info del torneo")
    @GetMapping("/{codigoPublico}")
    public ResponseEntity<ApiResponse<OrganizadorPublicoResponse>> organizador(
            @PathVariable String codigoPublico) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.obtenerOrganizador(codigoPublico), CodigoNegocio.S_SED_200_001));
    }

    @Operation(summary = "Sedes del torneo", description = "Dónde se juega: nombre y dirección de cada sede activa")
    @GetMapping("/{codigoPublico}/sedes")
    public ResponseEntity<ApiResponse<List<SedePublicaResponse>>> sedes(
            @PathVariable String codigoPublico) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarSedes(codigoPublico), CodigoNegocio.S_SED_200_002));
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

    @Operation(summary = "Tabla de posiciones")
    @GetMapping("/{codigoPublico}/tabla")
    public ResponseEntity<ApiResponse<List<TablaPosicionResponse>>> tabla(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.obtenerTabla(codigoPublico, edicionId, categoriaId),
                CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Partidos / fixture")
    @GetMapping("/{codigoPublico}/partidos")
    public ResponseEntity<ApiResponse<List<PartidoPublicoResponse>>> partidos(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarPartidos(codigoPublico, edicionId, categoriaId),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(
        summary = "Ranking de goleadores",
        description = "Máximos goleadores con asistencias. fase opcional: GRUPOS, FINAL_ORO, etc."
    )
    @GetMapping("/{codigoPublico}/goleadores")
    public ResponseEntity<ApiResponse<List<GoleadorResponse>>> goleadores(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId,
            @Parameter(description = "Filtrar por fase. Sin valor = todas las fases.")
            @RequestParam(required = false) String fase) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.rankingGoleadores(codigoPublico, edicionId, categoriaId, fase),
                CodigoNegocio.S_JUG_200_002));
    }

    @Operation(
        summary = "Ranking de tarjetas",
        description = "Jugadores con más tarjetas. Ordenado por rojas primero, luego amarillas."
    )
    @GetMapping("/{codigoPublico}/tarjetas")
    public ResponseEntity<ApiResponse<List<TarjetaResponse>>> tarjetas(
            @PathVariable String codigoPublico,
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId,
            @Parameter(description = "Filtrar por fase. Sin valor = todas las fases.")
            @RequestParam(required = false) String fase) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.rankingTarjetas(codigoPublico, edicionId, categoriaId, fase),
                CodigoNegocio.S_JUG_200_002));
    }

    @Operation(summary = "Plantel de un equipo",
            description = "Jugadores activos del equipo. No expone datos personales sensibles de menores.")
    @GetMapping("/{codigoPublico}/equipos/{equipoId}/plantel")
    public ResponseEntity<ApiResponse<PlantelResponse>> plantel(
            @PathVariable String codigoPublico,
            @PathVariable Long equipoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                publicService.listarPlantel(codigoPublico, equipoId),
                CodigoNegocio.S_JUG_200_002));
    }
}
