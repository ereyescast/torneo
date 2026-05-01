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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InscripcionJugadorServiceImpl implements InscripcionJugadorService {

    private final InscripcionJugadorRepository inscripcionRepository;
    private final JugadorRepository jugadorRepository;
    private final EquipoRepository equipoRepository;
    private final EdicionTorneoRepository edicionRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public InscripcionJugadorResponse inscribir(InscripcionJugadorRequest request) {

        // Validar que el jugador no esté ya inscrito en esta edición (Art. 22)
        if (inscripcionRepository.existsByJugadorIdAndEdicionId(
                request.getJugadorId(), request.getEdicionId())) {
            throw new BadRequestException(
                    "El jugador ya está inscrito en esta edición del torneo (Art. 22)");
        }

        Jugador jugador = jugadorRepository.findById(request.getJugadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Equipo equipo = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        // Validar edad del jugador según categoría del equipo (Art. III de las bases)
        validarEdadPorCategoria(jugador, equipo.getCategoria());

        // Validar cupo máximo por equipo (Art. 11 - F7: 18 jug, F9: 20 jug)
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

    // ────────────────────────────────────────────
    // Validaciones de reglas del torneo
    // ────────────────────────────────────────────

    /**
     * Art. III — Categorías:
     * FUTBOL 7: 2018-2019
     * FUTBOL 8: 2017
     * FUTBOL 9: 2014-2015-2016
     */
    private void validarEdadPorCategoria(Jugador jugador, Categoria categoria) {
        int anioNac = jugador.getFechaNacimiento().getYear();
        int anioCategoria = categoria.getAnioNacimiento();
        ModalidadJuego modalidad = categoria.getModalidad();

        boolean edadValida = switch (modalidad) {
            case FUTBOL_7 -> anioNac == anioCategoria; // un año por modalidad F7
            case FUTBOL_8 -> anioNac == anioCategoria;
            case FUTBOL_9 -> anioNac == anioCategoria;
            default -> true;
        };

        if (!edadValida) {
            throw new BadRequestException(String.format(
                    "El jugador (nacido en %d) no corresponde a la categoría %s %d (Art. III)",
                    anioNac, modalidad, anioCategoria));
        }
    }

    /**
     * Art. 11 — Cupo máximo por equipo:
     * FUTBOL 7: 18 jugadores
     * FUTBOL 9: 20 jugadores
     */
    private void validarCupoMaximo(Equipo equipo, EdicionTorneo edicion) {
        Categoria categoria = equipo.getCategoria();

        // Usar el maxJugadoresPorEquipo de la categoría si está definido,
        // si no usar el valor por defecto según las bases
        int maxPermitido;
        if (categoria.getMaxJugadoresPorEquipo() != null) {
            maxPermitido = categoria.getMaxJugadoresPorEquipo();
        } else {
            maxPermitido = switch (categoria.getModalidad()) {
                case FUTBOL_7 -> 18;
                case FUTBOL_8 -> 18;
                case FUTBOL_9 -> 20;
                default -> 20;
            };
        }

        long inscritos = inscripcionRepository.findByEquipoId(equipo.getId())
                .stream()
                .filter(InscripcionJugador::getActivo)
                .count();

        if (inscritos >= maxPermitido) {
            throw new BadRequestException(String.format(
                    "El equipo ya tiene %d jugadores inscritos. Máximo permitido: %d (Art. 11)",
                    inscritos, maxPermitido));
        }
    }

    // ────────────────────────────────────────────
    // Mapeo a DTO
    // ────────────────────────────────────────────

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
}
