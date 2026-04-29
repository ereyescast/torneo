package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import lombok.*;
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

    // Ver estadísticas de un partido
    @GetMapping("/partido/{partidoId}")
    public List<EstadisticaJugador> listarPorPartido(@PathVariable Long partidoId) {
        return estadisticaRepository.findByPartidoId(partidoId);
    }

    // Ver estadísticas de un jugador en una edición
    @GetMapping("/jugador/{jugadorId}/edicion/{edicionId}")
    public List<EstadisticaJugador> listarPorJugadorYEdicion(
            @PathVariable Long jugadorId,
            @PathVariable Long edicionId) {
        return estadisticaRepository.findByJugadorIdAndEdicionId(jugadorId, edicionId);
    }

    // Ver estadísticas de un equipo en una edición
    @GetMapping("/equipo/{equipoId}/edicion/{edicionId}")
    public List<EstadisticaJugador> listarPorEquipoYEdicion(
            @PathVariable Long equipoId,
            @PathVariable Long edicionId) {
        return estadisticaRepository.findByEquipoIdAndEdicionId(equipoId, edicionId);
    }

    // Registrar estadística de un jugador en un partido
    @PostMapping
    public ResponseEntity<EstadisticaJugador> registrar(
            @RequestParam Long jugadorId,
            @RequestParam Long partidoId,
            @RequestParam Long equipoId,
            @RequestParam Long edicionId,
            @RequestParam Long organizadorId,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados,
            @RequestParam(defaultValue = "false") Boolean titular) {

        // Validar que no exista ya estadística para ese jugador en ese partido
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

        EstadisticaJugador estadistica = EstadisticaJugador.builder()
                .organizadorId(organizadorId)
                .jugador(jugador)
                .partido(partido)
                .equipo(equipo)
                .edicion(edicion)
                .goles(goles)
                .asistencias(asistencias)
                .tarjetasAmarillas(tarjetasAmarillas)
                .tarjetasRojas(tarjetasRojas)
                .minutosJugados(minutosJugados)
                .titular(titular)
                .build();

        return ResponseEntity.ok(estadisticaRepository.save(estadistica));
    }

    // Actualizar estadística
    @PutMapping("/{id}")
    public ResponseEntity<EstadisticaJugador> actualizar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer goles,
            @RequestParam(defaultValue = "0") Integer asistencias,
            @RequestParam(defaultValue = "0") Integer tarjetasAmarillas,
            @RequestParam(defaultValue = "0") Integer tarjetasRojas,
            @RequestParam(required = false) Integer minutosJugados) {

        EstadisticaJugador estadistica = estadisticaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estadística no encontrada"));

        estadistica.setGoles(goles);
        estadistica.setAsistencias(asistencias);
        estadistica.setTarjetasAmarillas(tarjetasAmarillas);
        estadistica.setTarjetasRojas(tarjetasRojas);
        estadistica.setMinutosJugados(minutosJugados);

        return ResponseEntity.ok(estadisticaRepository.save(estadistica));
    }
}