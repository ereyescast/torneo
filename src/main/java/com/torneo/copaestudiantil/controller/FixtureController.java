package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.request.ConfiguracionCanchaRequest;
import com.torneo.copaestudiantil.dto.request.FixtureRequest;
import com.torneo.copaestudiantil.dto.request.GenerarFixtureRequest;
import com.torneo.copaestudiantil.dto.response.FixtureResponse;
import com.torneo.copaestudiantil.dto.response.PartidoResponse;
import com.torneo.copaestudiantil.service.FixtureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "13. Fixture", description = "Gestión del fixture semanal del torneo")
@RestController
@RequestMapping("/api/admin/fixtures")
@RequiredArgsConstructor
public class FixtureController {

    private final FixtureService fixtureService;

    @Operation(
            summary = "Crear fixture",
            description = """
            Crea un fixture en estado BORRADOR para una fecha del torneo.
            
            Flujo completo:
            1. POST /api/fixtures → crear en BORRADOR
            2. POST /api/fixtures/{id}/canchas → configurar canchas
            3. POST /api/fixtures/{id}/generar → generar partidos automáticamente
            4. PUT  /api/fixtures/{id}/publicar → publicar
            5. GET  /api/fixtures/{id}/pdf → descargar PDF para WhatsApp
            """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<FixtureResponse>> crear(
            @Valid @RequestBody FixtureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(fixtureService.crear(request),
                        CodigoNegocio.S_GRU_201_001));
    }

    @Operation(summary = "Obtener fixture por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FixtureResponse>> obtenerPorId(
            @Parameter(description = "ID del fixture") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(fixtureService.obtenerPorId(id),
                        CodigoNegocio.S_GRU_200_001));
    }

    @Operation(summary = "Listar fixtures de una edición")
    @GetMapping("/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<FixtureResponse>>> listarPorEdicion(
            @PathVariable Long edicionId) {
        return ResponseEntity.ok(
                ApiResponse.ok(fixtureService.listarPorEdicion(edicionId),
                        CodigoNegocio.S_GRU_200_002));
    }

    @Operation(
            summary = "Configurar canchas del fixture",
            description = """
            Define las canchas disponibles para ese día.
            Reemplaza la configuración anterior si ya existía.
            
            Ejemplo Copa Estudiantil (sábado típico):
            ```json
            [
              { "nombreCancha": "Campo 1", "modalidad": "FUTBOL_7", "horaInicio": "08:00", "horaFin": "13:00" },
              { "nombreCancha": "Campo 2", "modalidad": "FUTBOL_7", "horaInicio": "08:00", "horaFin": "13:00" },
              { "nombreCancha": "Campo 3", "modalidad": "FUTBOL_7", "horaInicio": "08:00", "horaFin": "13:00" },
              { "nombreCancha": "Campo 7", "modalidad": "FUTBOL_9", "horaInicio": "13:00", "horaFin": "18:00" },
              { "nombreCancha": "Campo 8", "modalidad": "FUTBOL_9", "horaInicio": "13:00", "horaFin": "18:00" }
            ]
            ```
            
            La duración por defecto es 40 min (F7 y F9 según Art. 27 de las bases).
            Puedes sobreescribir con `duracionPartidoMin` si el torneo usa otro tiempo.
            """
    )
    @PostMapping("/{id}/canchas")
    public ResponseEntity<ApiResponse<FixtureResponse>> configurarCanchas(
            @PathVariable Long id,
            @RequestBody List<@Valid ConfiguracionCanchaRequest> canchas) {
        return ResponseEntity.ok(
                ApiResponse.ok(fixtureService.configurarCanchas(id, canchas),
                        CodigoNegocio.S_GRU_200_001));
    }

    @Operation(
            summary = "Generar partidos automáticamente",
            description = """
            Distribuye los partidos en las canchas configuradas.
            
            El sistema:
            1. Lee las canchas y calcula cuántos partidos caben por cancha
            2. Genera todos los enfrentamientos (todos contra todos por grupo)
            3. Los distribuye en las canchas por modalidad respetando horarios
            4. Asigna cancha y hora a cada partido automáticamente
            
            Si no caben todos los partidos en las canchas configuradas, lanza error
            indicando que se deben agregar más canchas o ampliar el horario.
            
            ⚠️ Si ya existen partidos generados, lanza error 400.
            """
    )
    @PostMapping("/{id}/generar")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> generarPartidos(
            @PathVariable Long id,
            @RequestBody GenerarFixtureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(fixtureService.generarPartidos(id, request),
                        CodigoNegocio.S_PAR_201_001));
    }

    @Operation(
            summary = "Publicar fixture",
            description = "Cambia el estado a PUBLICADO. El fixture ya está listo para compartir por WhatsApp."
    )
    @PutMapping("/{id}/publicar")
    public ResponseEntity<ApiResponse<FixtureResponse>> publicar(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(fixtureService.publicar(id),
                        CodigoNegocio.S_GRU_200_001));
    }

    @Operation(summary = "Finalizar fixture", description = "Marca el fixture como FINALIZADO")
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<FixtureResponse>> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(fixtureService.finalizar(id),
                        CodigoNegocio.S_GRU_200_001));
    }

    @Operation(
            summary = "Eliminar fixture",
            description = "Solo se puede eliminar un fixture en estado BORRADOR. Elimina también sus partidos."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        fixtureService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_GRU_200_001));
    }
}
