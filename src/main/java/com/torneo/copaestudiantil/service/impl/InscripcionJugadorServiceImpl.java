package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.InscripcionJugadorRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.InscripcionJugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InscripcionJugadorServiceImpl implements InscripcionJugadorService {

    private final InscripcionJugadorRepository inscripcionRepository;
    private final JugadorRepository            jugadorRepository;
    private final EquipoRepository             equipoRepository;
    private final EdicionTorneoRepository      edicionRepository;
    private final CategoriaRepository          categoriaRepository;
    private final FixtureRepository            fixtureRepository;

    @Override
    public InscripcionJugadorResponse inscribir(InscripcionJugadorRequest request) {

        // ── Art. 22 — Validar inscripción duplicada ────────────────────────────
        if (inscripcionRepository.existsByJugadorIdAndEdicionId(
                request.getJugadorId(), request.getEdicionId())) {
            throw new BadRequestException(
                    "El jugador ya está inscrito en esta edición del torneo (Art. 22)");
        }

        Jugador jugador   = jugadorRepository.findById(request.getJugadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Equipo equipo     = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        // ── Art. 11 — Validar fecha límite de inscripción ─────────────────────
        // El padrón se puede enviar hasta antes del inicio del 2do tiempo de la 3ra fecha.
        // Simplificado: hasta la fecha del partido de la 3ra fecha del fixture.
        validarFechaLimiteInscripcion(request.getEdicionId());

        // ── Art. III — Validar edad por categoría ─────────────────────────────
        validarEdadPorCategoria(jugador, equipo.getCategoria());

        // ── Art. 11 — Validar cupo máximo ────────────────────────────────────
        validarCupoMaximo(equipo, edicion);

        InscripcionJugador inscripcion = InscripcionJugador.builder()
                .organizadorId(request.getOrganizadorId())
                .jugador(jugador)
                .equipo(equipo)
                .edicion(edicion)
                .activo(true)
                .build();

        return toResponse(inscripcionRepository.save(inscripcion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionJugadorResponse> listarPorEquipo(Long equipoId) {
        return inscripcionRepository.findByEquipoId(equipoId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionJugadorResponse> listarPorEdicion(Long edicionId) {
        return inscripcionRepository.findByEdicionId(edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public void desinscribir(Long id) {
        InscripcionJugador inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));
        inscripcion.setActivo(false);
        inscripcionRepository.save(inscripcion);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Validaciones de reglas del torneo
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Art. 11 — Fecha límite de inscripción:
     * "Tienen hasta la 3ra fecha (antes de empezar el 2do tiempo) para enviar el padrón."
     *
     * Implementación: busca el fixture de la fecha 3 de la edición.
     * Si ya pasó esa fecha, no se permiten más inscripciones.
     * Si no hay fixtures configurados, permite la inscripción (torneo en configuración).
     */
    private void validarFechaLimiteInscripcion(Long edicionId) {
        List<Fixture> fixtures = fixtureRepository.findByEdicionId(edicionId);
        if (fixtures.isEmpty()) return; // Sin fixtures = torneo en configuración, permitir

        Optional<Fixture> fechaTres = fixtures.stream()
                .filter(f -> f.getNumeroFecha() != null && f.getNumeroFecha() == 3)
                .findFirst();

        if (fechaTres.isPresent()) {
            LocalDate fechaLimite = fechaTres.get().getFechaTorneo();
            if (LocalDate.now().isAfter(fechaLimite)) {
                throw new BadRequestException(String.format(
                        "No se pueden agregar más jugadores. La fecha límite de inscripción "
                                + "fue el %s (3ra fecha del torneo — Art. 11). "
                                + "Contacta al organizador para casos excepcionales.",
                        fechaLimite));
            }
        }
    }

    /**
     * Art. III — Categorías:
     * FUTBOL 7: 2018-2019
     * FUTBOL 8: 2017
     * FUTBOL 9: 2014-2015-2016
     */
    private void validarEdadPorCategoria(Jugador jugador, Categoria categoria) {
        int anioNac      = jugador.getFechaNacimiento().getYear();
        int anioCategoria = categoria.getAnioNacimiento();

        if (anioNac != anioCategoria) {
            throw new BadRequestException(String.format(
                    "El jugador (nacido en %d) no corresponde a la categoría %s %d (Art. III)",
                    anioNac, categoria.getModalidad(), anioCategoria));
        }
    }

    /**
     * Art. 11 — Cupo máximo por equipo:
     * FUTBOL 7 / F8: 18 jugadores
     * FUTBOL 9:      20 jugadores
     */
    private void validarCupoMaximo(Equipo equipo, EdicionTorneo edicion) {
        Categoria categoria = equipo.getCategoria();

        int maxPermitido;
        if (categoria.getMaxJugadoresPorEquipo() != null) {
            maxPermitido = categoria.getMaxJugadoresPorEquipo();
        } else {
            maxPermitido = switch (categoria.getModalidad()) {
                case FUTBOL_7, FUTBOL_8 -> 18;
                case FUTBOL_9, FUTBOL_11 -> 20;
            };
        }

        long inscritos = inscripcionRepository.findByEquipoId(equipo.getId())
                .stream().filter(InscripcionJugador::getActivo).count();

        if (inscritos >= maxPermitido) {
            throw new BadRequestException(String.format(
                    "El equipo ya tiene %d jugadores inscritos. Máximo permitido: %d (Art. 11)",
                    inscritos, maxPermitido));
        }
    }

    // ── Mapeo a DTO ──────────────────────────────────────────────────────────

    private InscripcionJugadorResponse toResponse(InscripcionJugador i) {
        return InscripcionJugadorResponse.builder()
                .id(i.getId())
                .jugador(JugadorResponse.builder()
                        .id(i.getJugador().getId())
                        .nombres(i.getJugador().getNombres())
                        .apellidoPaterno(i.getJugador().getApellidoPaterno())
                        .apellidoMaterno(i.getJugador().getApellidoMaterno())
                        .tipoDocumento(i.getJugador().getTipoDocumento())
                        .numeroDocumento(i.getJugador().getNumeroDocumento())
                        .fechaNacimiento(i.getJugador().getFechaNacimiento())
                        .nacionalidad(i.getJugador().getNacionalidad())
                        .activo(i.getJugador().getActivo()).build())
                .equipo(EquipoResponse.builder()
                        .id(i.getEquipo().getId())
                        .nombre(i.getEquipo().getNombre())
                        .organizadorId(i.getEquipo().getOrganizadorId())
                        .activo(i.getEquipo().getActivo()).build())
                .edicion(EdicionTorneoResponse.builder()
                        .id(i.getEdicion().getId())
                        .nombre(i.getEdicion().getNombre())
                        .fechaInicio(i.getEdicion().getFechaInicio())
                        .fechaFin(i.getEdicion().getFechaFin())
                        .activa(i.getEdicion().getActiva()).build())
                .activo(i.getActivo())
                .fechaInscripcion(i.getFechaInscripcion())
                .build();
    }
}
