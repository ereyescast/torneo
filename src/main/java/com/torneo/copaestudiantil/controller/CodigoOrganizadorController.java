package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.CodigoOrganizadorResponse;
import com.torneo.copaestudiantil.service.CodigoOrganizadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gestión de códigos de invitación de organizador.
 * Solo el ADMIN de plataforma (validado en el service y en SecurityConfig).
 */
@Tag(name = "Códigos de organizador", description = "El admin invita organizadores con códigos de un solo uso")
@RestController
@RequestMapping("/api/admin/codigos-organizador")
@RequiredArgsConstructor
public class CodigoOrganizadorController {

    private final CodigoOrganizadorService codigoOrganizadorService;

    @Operation(summary = "Generar un código de invitación de organizador (un solo uso)")
    @PostMapping
    public ResponseEntity<ApiResponse<CodigoOrganizadorResponse>> generar(
            @RequestParam(value = "nota", required = false) String nota) {
        return ResponseEntity.ok(
                ApiResponse.ok(codigoOrganizadorService.generar(nota), CodigoNegocio.S_ORG_200_001));
    }

    @Operation(summary = "Listar códigos de organizador (ver cuáles se usaron)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CodigoOrganizadorResponse>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.ok(codigoOrganizadorService.listar(), CodigoNegocio.S_ORG_200_001));
    }
}
