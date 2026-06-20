package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.InscripcionJugadorRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.service.InscripcionJugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final FixtureRepository            fixtureRepository;

    @Override
    public InscripcionJugadorResponse inscribir(InscripcionJugadorRequest request) {

        // ── Art. 22 — Validar inscripción duplicada ───────────────────────────
        if (inscripcionRepository.existsByJugadorIdAndEdicionId(
                request.getJugadorId(), request.getEdicionId()))
            throw new BadRequestException(
                    "El jugador ya está inscrito en esta edición del torneo (Art. 22)");

        Jugador jugador = jugadorRepository.findById(request.getJugadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        Equipo equipo = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        // ── Seguridad — un delegado solo inscribe en SU equipo ───────────────
        SecurityUtils.validarPertenencia(equipo.getOrganizadorId());
        SecurityUtils.validarEquipoDelegado(equipo.getId());

        // ── Art. 11 — Validar fecha límite de inscripción ─────────────────────
        validarFechaLimiteInscripcion(request.getEdicionId());

        // ── Art. III + Art. 22 — Validar edad por categoría ──────────────────
        validarEdadPorCategoria(jugador, equipo.getCategoria());

        // ── Art. 11 — Validar cupo máximo ─────────────────────────────────────
        validarCupoMaximo(equipo);

        InscripcionJugador inscripcion = InscripcionJugador.builder()
                .organizadorId(SecurityUtils.getOrganizadorIdActual())
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
    @Transactional(readOnly = true)
    public MiEquipoDelegadoResponse miContextoDelegado() {
        Long equipoId = SecurityUtils.getEquipoIdActual();
        if (equipoId == null)
            throw new BadRequestException("Este usuario no está asignado a ningún equipo.");
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        SecurityUtils.validarPertenencia(equipo.getOrganizadorId());
        return MiEquipoDelegadoResponse.builder()
                .equipoId(equipo.getId())
                .equipoNombre(equipo.getNombre())
                .edicionId(equipo.getEdicion() != null ? equipo.getEdicion().getId() : null)
                .edicionNombre(equipo.getEdicion() != null ? equipo.getEdicion().getNombre() : null)
                .categoriaId(equipo.getCategoria() != null ? equipo.getCategoria().getId() : null)
                .build();
    }

    @Override
    public void desinscribir(Long id) {
        InscripcionJugador inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));
        // Seguridad: pertenencia de organizador + (si es delegado) su propio equipo
        SecurityUtils.validarPertenencia(inscripcion.getOrganizadorId());
        SecurityUtils.validarEquipoDelegado(inscripcion.getEquipo().getId());
        inscripcion.setActivo(false);
        inscripcionRepository.save(inscripcion);
    }

    // ── Validaciones ──────────────────────────────────────────────────────────

    /**
     * Art. 11 — Fecha límite de inscripción.
     * "Tienen hasta la 3ra fecha para enviar el padrón."
     * Si no hay fixtures configurados, permite la inscripción.
     */
    private void validarFechaLimiteInscripcion(Long edicionId) {
        List<Fixture> fixtures = fixtureRepository.findByEdicionId(edicionId);
        if (fixtures.isEmpty()) return;

        Optional<Fixture> fechaTres = fixtures.stream()
                .filter(f -> f.getNumeroFecha() != null && f.getNumeroFecha() == 3)
                .findFirst();

        if (fechaTres.isPresent()) {
            LocalDate fechaLimite = fechaTres.get().getFechaTorneo();
            if (LocalDate.now().isAfter(fechaLimite))
                throw new BadRequestException(String.format(
                        "La fecha límite de inscripción fue el %s (3ra fecha — Art. 11). "
                                + "No se pueden agregar más jugadores.",
                        fechaLimite));
        }
    }

    /**
     * Art. III + Art. 22 — Validación de edad por categoría.
     *
     * Regla base (todos los jugadores):
     *   El jugador debe ser igual o más joven que el año de la categoría.
     *   ✅ Nacido 2019 en categoría 2018 → más joven, sin ventaja física
     *   ✅ Nacido 2018 en categoría 2018 → año exacto
     *   ❌ Nacido 2017 en categoría 2018 → mayor, tiene ventaja física
     *
     * Excepción Art. 22 (solo niñas en competitivo):
     *   Una niña puede jugar exactamente 1 año por encima de su categoría.
     *   ✅ Niña nacida 2017 en categoría F7 (2018) → excepción Art. 22
     *   ❌ Niño nacido 2017 en categoría F7 (2018) → no aplica la excepción
     */
    private void validarEdadPorCategoria(Jugador jugador, Categoria categoria) {
        int anioNac       = jugador.getFechaNacimiento().getYear();
        int anioCategoria = categoria.getAnioNacimiento();

        // Regla base: igual o más joven
        if (anioNac >= anioCategoria) return;

        // Verificar excepción Art. 22 — niña en competitivo
        boolean esNina       = Genero.FEMENINO.equals(jugador.getGenero());
        boolean esCompetitivo = NivelCompetencia.COMPETITIVO.equals(categoria.getNivel());

        if (esNina && esCompetitivo && anioNac == anioCategoria - 1) return;

        // No cumple ninguna regla
        if (esNina && esCompetitivo) {
            throw new BadRequestException(String.format(
                    "La jugadora (nacida en %d) no puede jugar en la categoría %s %d. "
                            + "La excepción del Art. 22 permite jugar máximo 1 año por encima "
                            + "(año %d como mínimo).",
                    anioNac, categoria.getModalidad(), anioCategoria, anioCategoria - 1));
        }

        throw new BadRequestException(String.format(
                "El jugador (nacido en %d) no puede jugar en la categoría %s %d. "
                        + "Solo pueden inscribirse jugadores nacidos en %d o años posteriores "
                        + "(Art. III).",
                anioNac, categoria.getModalidad(), anioCategoria, anioCategoria));
    }

    /**
     * Art. 11 — Cupo máximo por equipo.
     * F7 y F8: 18 jugadores. F9 y F11: 20 jugadores.
     */
    private void validarCupoMaximo(Equipo equipo) {
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

        if (inscritos >= maxPermitido)
            throw new BadRequestException(String.format(
                    "El equipo ya tiene %d jugadores inscritos. Máximo permitido: %d (Art. 11)",
                    inscritos, maxPermitido));
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────

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
