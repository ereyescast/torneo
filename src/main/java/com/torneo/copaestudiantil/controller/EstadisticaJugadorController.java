package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.SuspensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "14. Estadísticas", description = "Goles, asistencias y tarjetas por jugador en cada partido")
@RestController
@RequestMapping("/api/admin/estadisticas")
@RequiredArgsConstructor
public class EstadisticaJugadorController {

    private final EstadisticaJugadorRepository estadisticaRepository;
    private final JugadorRepository            jugadorRepository;
    private final PartidoRepository            partidoRepository;
    private final EquipoRepository             equipoRepository;
    private final EdicionTorneoRepository      edicionTorneoRepository;
    private final SuspensionService            suspensionService;

    private EstadisticaJugadorResponse toResponse(EstadisticaJugador est) {
        Jugador j = est.getJugador();
        Equipo eq = est.getEquipo();
        EdicionTorneo ed = est.getEdicion();
        return EstadisticaJugadorResponse.builder()
                .id(est.getId())
                .organizadorId(est.getOrganizadorId())
                .partidoId(est.getPartido().getId())
                .goles(est.getGoles())
                .asistencias(est.getAsistencias())
                .tarjetasAmarillas(est.getTarjetasAmarillas())
                .tarjetasRojas(est.getTarjetasRojas())
                .minutosJugados(est.getMinutosJugados())
                .titular(est.getTitular())
                .jugador(JugadorResponse.builder()
                        .id(j.getId()).nombres(j.getNombres())
                        .apellidoPaterno(j.getApellidoPaterno())
                        .apellidoMaterno(j.getApellidoMaterno())
                        .tipoDocumento(j.getTipoDocumento())
                        .numeroDocumento(j.getNumeroDocumento())
                        .fechaNacimiento(j.getFechaNacimiento())
                        .nacionalidad(j.getNacionalidad())
                        .activo(j.getActivo()).build())
                .equipo(EquipoResponse.builder()
                        .id(eq.getId()).nombre(eq.getNombre())
                        .organizadorId(eq.getOrganizadorId())
                        .activo(eq.getActivo()).build())
                .edicion(EdicionTorneoResponse.builder()
                        .id(ed.getId()).nombre(ed.getNombre())
                        .fechaInicio(ed.getFechaInicio()).fechaFin(ed.getFechaFin())
                        .activa(ed.getActiva()).build())
                .build();
    }

    @Operation(summary = "Estadísticas de un partido")
    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorPartido(
            @PathVariable Long partidoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaRepository.findByPartidoId(partidoId)
                        .stream().map(this::toResponse).toList(),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Estadísticas de un jugador en una edición")
    @GetMapping("/jugador/{jugadorId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorJugadorYEdicion(
            @PathVariable Long jugadorId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaRepository.findByJugadorIdAndEdicionId(jugadorId, edicionId)
                        .stream().map(this::toResponse).toList(),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(summary = "Estadísticas de un equipo en una edición")
    @GetMapping("/equipo/{equipoId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorEquipoYEdicion(
            @PathVariable Long equipoId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaRepository.findByEquipoIdAndEdicionId(equipoId, edicionId)
                        .stream().map(this::toResponse).toList(),
                CodigoNegocio.S_PAR_200_002));
    }

    @Operation(
        summary = "Registrar estadística",
        description = """
            Registra las estadísticas de un jugador en un partido.
            
            ART. 23 — Si se registra tarjeta roja (tarjetasRojas >= 1),
            el sistema crea automáticamente una suspensión para la siguiente fecha.
            ART. 23 — Las tarjetas amarillas NO acumulan entre partidos.
            
            Requiere el `numeroFecha` actual para calcular la suspensión.
            """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<EstadisticaJugadorResponse>> registrar(
            @Parameter(description = "ID del jugador") @RequestParam Long jugadorId,
            @Parameter(description = "ID del partido") @RequestParam Long partidoId,
            @RequestParam Long equipoId,
            @RequestParam Long edicionId,
            @RequestParam Long organizadorId,
            @Parameter(description = "Número de fecha actual del torneo (1-6). Necesario para calcular suspensión por tarjeta roja")
            @RequestParam Integer numeroFecha,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @Parameter(description = "1 roja = suspensión automática siguiente fecha (Art. 23)")
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados,
            @RequestParam(defaultValue = "false") Boolean titular) {

        if (estadisticaRepository.findByJugadorIdAndPartidoId(jugadorId, partidoId).isPresent())
            throw new BadRequestException("Ya existe estadística para este jugador en este partido");

        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo ed = edicionTorneoRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        EstadisticaJugador est = EstadisticaJugador.builder()
                .organizadorId(organizadorId).jugador(jugador).partido(partido)
                .equipo(equipo).edicion(ed).goles(goles).asistencias(asistencias)
                .tarjetasAmarillas(tarjetasAmarillas).tarjetasRojas(tarjetasRojas)
                .minutosJugados(minutosJugados).titular(titular).build();

        EstadisticaJugador guardada = estadisticaRepository.save(est);

        // ART. 23 — Procesar suspensión automática si hay tarjeta roja
        if (tarjetasRojas > 0) {
            suspensionService.procesarTarjetaRoja(guardada, numeroFecha);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(guardada), CodigoNegocio.S_PAR_200_003));
    }

    @Operation(summary = "Actualizar estadística")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EstadisticaJugadorResponse>> actualizar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados) {

        EstadisticaJugador est = estadisticaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estadística no encontrada"));
        est.setGoles(goles);
        est.setAsistencias(asistencias);
        est.setTarjetasAmarillas(tarjetasAmarillas);
        est.setTarjetasRojas(tarjetasRojas);
        est.setMinutosJugados(minutosJugados);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(estadisticaRepository.save(est)),
                CodigoNegocio.S_PAR_200_003));
    }
}
