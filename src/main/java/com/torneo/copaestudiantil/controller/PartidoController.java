package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.request.PartidoRequest;
import com.torneo.copaestudiantil.dto.response.PartidoResponse;
import com.torneo.copaestudiantil.entity.FasePartido;
import com.torneo.copaestudiantil.service.PartidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
@RequiredArgsConstructor
public class PartidoController {

    // Solo inyecta el service — ningún repository directo
    private final PartidoService partidoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listar(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorEdicionYCategoria(edicionId, categoriaId),
                CodigoNegocio.S_PAR_200_002));
    }

    @GetMapping("/fase")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listarPorFase(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId,
            @RequestParam FasePartido fase) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorFase(edicionId, categoriaId, fase),
                CodigoNegocio.S_PAR_200_002));
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listarPorGrupo(
            @PathVariable Long grupoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorGrupo(grupoId),
                CodigoNegocio.S_PAR_200_002));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartidoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.obtenerPorId(id),
                CodigoNegocio.S_PAR_200_001));
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listarPorEquipo(
            @PathVariable Long equipoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorEquipo(equipoId),
                CodigoNegocio.S_PAR_200_002));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PartidoResponse>> crear(
            @Valid @RequestBody PartidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        partidoService.crear(request),
                        CodigoNegocio.S_PAR_201_001));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<PartidoResponse>> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.iniciar(id),
                CodigoNegocio.S_PAR_200_003));
    }

    @PutMapping("/{id}/suspender")
    public ResponseEntity<ApiResponse<PartidoResponse>> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.suspender(id),
                CodigoNegocio.S_PAR_200_003));
    }

    @PutMapping("/{id}/resultado")
    public ResponseEntity<ApiResponse<PartidoResponse>> registrarResultado(
            @PathVariable Long id,
            @RequestParam Integer golesLocal,
            @RequestParam Integer golesVisitante) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.registrarResultado(id, golesLocal, golesVisitante),
                CodigoNegocio.S_PAR_200_003));
    }

    @PutMapping("/{id}/wo")
    public ResponseEntity<ApiResponse<PartidoResponse>> registrarWo(
            @PathVariable Long id,
            @RequestParam Long equipoWoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.registrarWo(id, equipoWoId),
                CodigoNegocio.S_PAR_200_004));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelar(@PathVariable Long id) {
        partidoService.cancelar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_PAR_200_003));
    }
}
