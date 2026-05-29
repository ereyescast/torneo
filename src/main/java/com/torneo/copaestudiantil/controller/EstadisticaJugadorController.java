package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaJugadorController {

    private final EstadisticaJugadorRepository estadisticaRepository;
    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;
    private final EquipoRepository equipoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;

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

    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorPartido(
            @PathVariable Long partidoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaRepository.findByPartidoId(partidoId)
                        .stream().map(this::toResponse).toList(),
                CodigoNegocio.S_PAR_200_002));
    }

    @GetMapping("/jugador/{jugadorId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorJugadorYEdicion(
            @PathVariable Long jugadorId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaRepository.findByJugadorIdAndEdicionId(jugadorId, edicionId)
                        .stream().map(this::toResponse).toList(),
                CodigoNegocio.S_PAR_200_002));
    }

    @GetMapping("/equipo/{equipoId}/edicion/{edicionId}")
    public ResponseEntity<ApiResponse<List<EstadisticaJugadorResponse>>> listarPorEquipoYEdicion(
            @PathVariable Long equipoId, @PathVariable Long edicionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                estadisticaRepository.findByEquipoIdAndEdicionId(equipoId, edicionId)
                        .stream().map(this::toResponse).toList(),
                CodigoNegocio.S_PAR_200_002));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EstadisticaJugadorResponse>> registrar(
            @RequestParam Long jugadorId, @RequestParam Long partidoId,
            @RequestParam Long equipoId, @RequestParam Long edicionId,
            @RequestParam Long organizadorId,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados,
            @RequestParam(defaultValue = "false") Boolean titular) {

        if (estadisticaRepository.findByJugadorIdAndPartidoId(jugadorId, partidoId).isPresent()) {
            throw new BadRequestException("Ya existe estadística para este jugador en este partido");
        }

        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionTorneoRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        EstadisticaJugador est = EstadisticaJugador.builder()
                .organizadorId(organizadorId).jugador(jugador).partido(partido)
                .equipo(equipo).edicion(edicion).goles(goles).asistencias(asistencias)
                .tarjetasAmarillas(tarjetasAmarillas).tarjetasRojas(tarjetasRojas)
                .minutosJugados(minutosJugados).titular(titular).build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(estadisticaRepository.save(est)),
                        CodigoNegocio.S_PAR_200_003));
    }

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
