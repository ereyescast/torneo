package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.request.PagoEquipoRequest;
import com.torneo.copaestudiantil.dto.response.PagoEquipoResponse;
import com.torneo.copaestudiantil.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "17. Pagos", description = "Control manual de pagos: inscripción, arbitraje y multas")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @Operation(
        summary = "Registrar pago",
        description = """
            Registra un pago de inscripción, arbitraje o multa.
            
            Tipos de pago (XIV — Bases):
            - INSCRIPCION: F7=S/.350, F8=S/.400, F9=S/.500
            - ARBITRAJE: F7=S/.45, F8=S/.60, F9=S/.70 por partido
            - MULTA_WO: S/.50 (Art. 16a) — se genera automáticamente al registrar un WO
            - MULTA_RECLAMO: S/.50 (Art. 40)
            
            ART. 11 — Cancelación total de inscripciones hasta la 2da fecha.
            """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PagoEquipoResponse>> registrar(
            @Valid @RequestBody PagoEquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(pagoService.registrar(request),
                        CodigoNegocio.S_EQU_201_001));
    }

    @Operation(
        summary = "Confirmar pago",
        description = "El organizador confirma que recibió el pago. Cambia estado a PAGADO."
    )
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ApiResponse<PagoEquipoResponse>> confirmar(
            @PathVariable Long id,
            @RequestParam(required = false) String referenciaPago) {
        return ResponseEntity.ok(ApiResponse.ok(
                pagoService.confirmarPago(id, referenciaPago),
                CodigoNegocio.S_EQU_200_003));
    }

    @Operation(summary = "Marcar pago como vencido")
    @PutMapping("/{id}/vencer")
    public ResponseEntity<ApiResponse<PagoEquipoResponse>> marcarVencido(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                pagoService.marcarVencido(id),
                CodigoNegocio.S_EQU_200_003));
    }

    @Operation(summary = "Pagos de un equipo en una edición")
    @GetMapping("/equipo/{equipoId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<PagoEquipoResponse>>> listarPorEquipo(
            @PathVariable Long equipoId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                pagoService.listarPorEquipo(equipoId, edicionId),
                CodigoNegocio.S_EQU_200_002));
    }

    @Operation(summary = "Todos los pagos de una edición")
    @GetMapping("/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<PagoEquipoResponse>>> listarPorEdicion(
            @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                pagoService.listarPorEdicion(edicionId),
                CodigoNegocio.S_EQU_200_002));
    }

    @Operation(
        summary = "Equipos deudores",
        description = "Lista equipos con pagos PENDIENTES o VENCIDOS en la edición"
    )
    @GetMapping("/edicion/{edicionId}/deudores")
    public ResponseEntity<ApiResponse<List<PagoEquipoResponse>>> listarDeudores(
            @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                pagoService.listarDeudores(edicionId),
                CodigoNegocio.S_EQU_200_002));
    }

    @Operation(
        summary = "Inicializar pago de inscripción",
        description = "Crea el registro de inscripción con estado PENDIENTE. El monto se calcula automáticamente según la modalidad del equipo."
    )
    @PostMapping("/inscripcion")
    public ResponseEntity<ApiResponse<PagoEquipoResponse>> inicializarInscripcion(
            @RequestParam Long equipoId,
            @RequestParam Long edicionId,
            @RequestParam Long organizadorId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        pagoService.inicializarInscripcion(equipoId, edicionId, organizadorId),
                        CodigoNegocio.S_EQU_201_001));
    }
}
