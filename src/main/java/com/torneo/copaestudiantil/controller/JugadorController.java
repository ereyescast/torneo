package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import com.torneo.copaestudiantil.entity.Jugador;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.JugadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jugadores")
@RequiredArgsConstructor
public class JugadorController {

    private final JugadorRepository jugadorRepository;

    private JugadorResponse toResponse(Jugador j) {
        return JugadorResponse.builder()
                .id(j.getId())
                .nombres(j.getNombres())
                .apellidoPaterno(j.getApellidoPaterno())
                .apellidoMaterno(j.getApellidoMaterno())
                .tipoDocumento(j.getTipoDocumento())
                .numeroDocumento(j.getNumeroDocumento())
                .fechaNacimiento(j.getFechaNacimiento())
                .nacionalidad(j.getNacionalidad())
                .profileImage(j.getProfileImage())
                .activo(j.getActivo())
                .build();
    }

    @GetMapping
    public List<JugadorResponse> listar() {
        return jugadorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JugadorResponse> buscarPorId(@PathVariable Long id) {
        return jugadorRepository.findById(id)
                .map(j -> ResponseEntity.ok(toResponse(j)))
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
    }

    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<JugadorResponse> buscarPorDocumento(
            @PathVariable String numeroDocumento) {
        return jugadorRepository.findByNumeroDocumento(numeroDocumento)
                .map(j -> ResponseEntity.ok(toResponse(j)))
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
    }

    @PostMapping
    public ResponseEntity<JugadorResponse> crear(@RequestBody JugadorRequest request) {
        if (jugadorRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new IllegalArgumentException("Ya existe un jugador con ese documento");
        }
        Jugador jugador = Jugador.builder()
                .nombres(request.getNombres())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .fechaNacimiento(request.getFechaNacimiento())
                .nacionalidad(request.getNacionalidad())
                .activo(true)
                .build();
        return ResponseEntity.ok(toResponse(jugadorRepository.save(jugador)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JugadorResponse> actualizar(
            @PathVariable Long id,
            @RequestBody JugadorRequest request) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        jugador.setNombres(request.getNombres());
        jugador.setApellidoPaterno(request.getApellidoPaterno());
        jugador.setApellidoMaterno(request.getApellidoMaterno());
        jugador.setTipoDocumento(request.getTipoDocumento());
        jugador.setFechaNacimiento(request.getFechaNacimiento());
        jugador.setNacionalidad(request.getNacionalidad());
        return ResponseEntity.ok(toResponse(jugadorRepository.save(jugador)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        jugador.setActivo(false);
        jugadorRepository.save(jugador);
        return ResponseEntity.ok().build();
    }
}