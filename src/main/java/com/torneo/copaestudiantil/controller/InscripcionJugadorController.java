package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.InscripcionJugadorRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionJugadorController {

    private final InscripcionJugadorRepository inscripcionRepository;
    private final JugadorRepository jugadorRepository;
    private final EquipoRepository equipoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;

    private InscripcionJugadorResponse toResponse(InscripcionJugador i) {
        JugadorResponse jugadorResponse = JugadorResponse.builder()
                .id(i.getJugador().getId())
                .nombres(i.getJugador().getNombres())
                .apellidoPaterno(i.getJugador().getApellidoPaterno())
                .apellidoMaterno(i.getJugador().getApellidoMaterno())
                .tipoDocumento(i.getJugador().getTipoDocumento())
                .numeroDocumento(i.getJugador().getNumeroDocumento())
                .fechaNacimiento(i.getJugador().getFechaNacimiento())
                .nacionalidad(i.getJugador().getNacionalidad())
                .activo(i.getJugador().getActivo())
                .build();

        EquipoResponse equipoResponse = EquipoResponse.builder()
                .id(i.getEquipo().getId())
                .nombre(i.getEquipo().getNombre())
                .organizadorId(i.getEquipo().getOrganizadorId())
                .activo(i.getEquipo().getActivo())
                .build();

        EdicionTorneoResponse edicionResponse = EdicionTorneoResponse.builder()
                .id(i.getEdicion().getId())
                .nombre(i.getEdicion().getNombre())
                .fechaInicio(i.getEdicion().getFechaInicio())
                .fechaFin(i.getEdicion().getFechaFin())
                .activa(i.getEdicion().getActiva())
                .build();

        return InscripcionJugadorResponse.builder()
                .id(i.getId())
                .jugador(jugadorResponse)
                .equipo(equipoResponse)
                .edicion(edicionResponse)
                .activo(i.getActivo())
                .fechaInscripcion(i.getFechaInscripcion())
                .build();
    }

    // Listar jugadores de un equipo
    @GetMapping("/equipo/{equipoId}")
    public List<InscripcionJugadorResponse> listarPorEquipo(@PathVariable Long equipoId) {
        return inscripcionRepository.findByEquipoId(equipoId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Listar jugadores de una edición
    @GetMapping("/edicion/{edicionId}")
    public List<InscripcionJugadorResponse> listarPorEdicion(@PathVariable Long edicionId) {
        return inscripcionRepository.findByEdicionId(edicionId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Inscribir jugador a equipo
    @PostMapping
    public ResponseEntity<InscripcionJugadorResponse> inscribir(
            @RequestBody InscripcionJugadorRequest request) {

        // Validar que el jugador no esté ya inscrito en esta edición
        if (inscripcionRepository.existsByJugadorIdAndEdicionId(
                request.getJugadorId(), request.getEdicionId())) {
            throw new BadRequestException("El jugador ya está inscrito en esta edición");
        }

        Jugador jugador = jugadorRepository.findById(request.getJugadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Equipo equipo = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionTorneoRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        InscripcionJugador inscripcion = InscripcionJugador.builder()
                .organizadorId(request.getOrganizadorId())
                .jugador(jugador)
                .equipo(equipo)
                .edicion(edicion)
                .activo(true)
                .build();

        return ResponseEntity.ok(toResponse(inscripcionRepository.save(inscripcion)));
    }

    // Desinscribir jugador (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desinscribir(@PathVariable Long id) {
        InscripcionJugador inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));
        inscripcion.setActivo(false);
        inscripcionRepository.save(inscripcion);
        return ResponseEntity.ok().build();
    }
}