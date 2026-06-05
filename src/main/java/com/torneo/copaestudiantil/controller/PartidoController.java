package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.request.PartidoRequest;
import com.torneo.copaestudiantil.dto.response.HistorialEquipoResponse;
import com.torneo.copaestudiantil.dto.response.PartidoResponse;
import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.EquipoRepository;
import com.torneo.copaestudiantil.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "11. Partidos", description = "Gestión y control de partidos del torneo")
@RestController
@RequestMapping("/api/admin/partidos")
@RequiredArgsConstructor
public class PartidoController {

    private final PartidoService  partidoService;
    private final EquipoRepository equipoRepository;

    @Operation(summary = "Listar partidos por edición y categoría")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listar(
            @RequestParam Long edicionId, @RequestParam Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorEdicionYCategoria(edicionId, categoriaId),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Listar por fase")
    @GetMapping("/fase")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listarPorFase(
            @RequestParam Long edicionId, @RequestParam Long categoriaId,
            @RequestParam FasePartido fase) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorFase(edicionId, categoriaId, fase),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Listar partidos de un grupo")
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listarPorGrupo(
            @PathVariable Long grupoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorGrupo(grupoId),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Obtener partido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartidoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.obtenerPorId(id), CodigoNegocio.S_PAR_200_001));
    }

    @Operation(summary = "Listar partidos de un equipo en la edición actual")
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<ApiResponse<List<PartidoResponse>>> listarPorEquipo(
            @PathVariable Long equipoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.listarPorEquipo(equipoId), CodigoNegocio.S_PAR_200_002));
    }

    @Operation(
        summary = "Historial de un equipo en una edición",
        description = """
            Art. 14 — Muestra todos los partidos de un equipo en una edición específica.
            
            Incluye:
            - Lista de partidos ordenada por fecha
            - Resumen estadístico (victorias, empates, derrotas, goles, puntos)
            """
    )
    @GetMapping("/equipo/{equipoId}/historial")
    public ResponseEntity<ApiResponse<HistorialEquipoResponse>> historialPorEdicion(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "ID de la edición") @RequestParam Long edicionId) {

        var equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));

        List<PartidoResponse> partidos = partidoService
                .historialPorEquipoYEdicion(equipoId, edicionId);

        // Calcular resumen estadístico
        HistorialEquipoResponse.ResumenEstadistico resumen = calcularResumen(
                partidos, equipoId);

        var equipoResp = com.torneo.copaestudiantil.dto.response.EquipoResponse.builder()
                .id(equipo.getId()).nombre(equipo.getNombre())
                .organizadorId(equipo.getOrganizadorId())
                .activo(equipo.getActivo()).build();

        return ResponseEntity.ok(ApiResponse.ok(
                HistorialEquipoResponse.builder()
                        .equipo(equipoResp)
                        .partidos(partidos)
                        .resumen(resumen)
                        .build(),
                CodigoNegocio.S_EQU_200_001));
    }

    @Operation(
        summary = "Historial completo de un equipo",
        description = "Muestra todos los partidos del equipo en todas las ediciones históricas"
    )
    @GetMapping("/equipo/{equipoId}/historial/completo")
    public ResponseEntity<ApiResponse<HistorialEquipoResponse>> historialCompleto(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId) {

        var equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));

        List<PartidoResponse> partidos = partidoService.historialCompleto(equipoId);

        HistorialEquipoResponse.ResumenEstadistico resumen = calcularResumen(
                partidos, equipoId);

        var equipoResp = com.torneo.copaestudiantil.dto.response.EquipoResponse.builder()
                .id(equipo.getId()).nombre(equipo.getNombre())
                .organizadorId(equipo.getOrganizadorId())
                .activo(equipo.getActivo()).build();

        return ResponseEntity.ok(ApiResponse.ok(
                HistorialEquipoResponse.builder()
                        .equipo(equipoResp)
                        .partidos(partidos)
                        .resumen(resumen)
                        .build(),
                CodigoNegocio.S_EQU_200_001));
    }

    @Operation(summary = "Crear partido")
    @PostMapping
    public ResponseEntity<ApiResponse<PartidoResponse>> crear(
            @Valid @RequestBody PartidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(partidoService.crear(request),
                        CodigoNegocio.S_PAR_201_001));
    }

    @Operation(summary = "Iniciar partido", description = "PROGRAMADO → EN_JUEGO")
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<PartidoResponse>> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.iniciar(id), CodigoNegocio.S_PAR_200_003));
    }

    @Operation(summary = "Suspender partido")
    @PutMapping("/{id}/suspender")
    public ResponseEntity<ApiResponse<PartidoResponse>> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.suspender(id), CodigoNegocio.S_PAR_200_003));
    }

    @Operation(
        summary = "Registrar resultado",
        description = "El partido debe estar EN_JUEGO. Actualiza la tabla de posiciones automáticamente."
    )
    @PutMapping("/{id}/resultado")
    public ResponseEntity<ApiResponse<PartidoResponse>> registrarResultado(
            @PathVariable Long id,
            @RequestParam Integer golesLocal,
            @RequestParam Integer golesVisitante) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.registrarResultado(id, golesLocal, golesVisitante),
                CodigoNegocio.S_PAR_200_003));
    }

    @Operation(
        summary = "Registrar WO",
        description = """
            Aplica WO al equipo indicado (pierde 2-0).
            
            Automáticamente:
            - Genera multa S/.50 al equipo (Art. 16a)
            - Si acumula 2 WOs en la edición, elimina al equipo del torneo (Art. 16b)
            - Actualiza la tabla de posiciones
            """
    )
    @PutMapping("/{id}/wo")
    public ResponseEntity<ApiResponse<PartidoResponse>> registrarWo(
            @PathVariable Long id,
            @Parameter(description = "ID del equipo que incurre en WO")
            @RequestParam Long equipoWoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                partidoService.registrarWo(id, equipoWoId),
                CodigoNegocio.S_PAR_200_004));
    }

    @Operation(summary = "Cancelar partido")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelar(@PathVariable Long id) {
        partidoService.cancelar(id);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_PAR_200_003));
    }

    // ── Cálculo de resumen estadístico ────────────────────────────────────────

    private HistorialEquipoResponse.ResumenEstadistico calcularResumen(
            List<PartidoResponse> partidos, Long equipoId) {

        int victorias = 0, empates = 0, derrotas = 0;
        int golesFavor = 0, golesContra = 0, wos = 0;

        for (PartidoResponse p : partidos) {
            if (p.getGolesLocal() == null || p.getGolesVisitante() == null) continue;

            boolean esLocal = p.getEquipoLocal().getId().equals(equipoId);
            int gF = esLocal ? p.getGolesLocal()    : p.getGolesVisitante();
            int gC = esLocal ? p.getGolesVisitante() : p.getGolesLocal();

            golesFavor  += gF;
            golesContra += gC;

            if (EstadoPartido.WO.name().equals(p.getEstado().name()) && gF == 0) {
                wos++;
                derrotas++;
            } else if (gF > gC) {
                victorias++;
            } else if (gF < gC) {
                derrotas++;
            } else {
                empates++;
            }
        }

        int totalPartidos = victorias + empates + derrotas;
        int puntos = victorias * 3 + empates;

        return HistorialEquipoResponse.ResumenEstadistico.builder()
                .totalPartidos(totalPartidos)
                .victorias(victorias)
                .empates(empates)
                .derrotas(derrotas)
                .golesFavor(golesFavor)
                .golesContra(golesContra)
                .diferenciaGol(golesFavor - golesContra)
                .puntos(puntos)
                .wos(wos)
                .build();
    }
}
