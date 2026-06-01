package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import com.torneo.copaestudiantil.dto.response.SuspensionResponse;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.SuspensionJugadorRepository;
import com.torneo.copaestudiantil.service.SuspensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SuspensionServiceImpl implements SuspensionService {

    private final SuspensionJugadorRepository suspensionRepository;

    @Override
    public void procesarTarjetaRoja(EstadisticaJugador estadistica, Integer fechaActual) {
        if (estadistica.getTarjetasRojas() == null || estadistica.getTarjetasRojas() <= 0)
            return;

        // ART. 23 — suspensión por 1 fecha
        SuspensionJugador suspension = SuspensionJugador.builder()
                .organizadorId(estadistica.getOrganizadorId())
                .jugador(estadistica.getJugador())
                .edicion(estadistica.getEdicion())
                .partidoOrigen(estadistica.getPartido())
                .fechaOrigen(fechaActual)
                .fechaSuspension(fechaActual + 1)
                .activo(true)
                .motivo("Expulsión con tarjeta roja — Art. 23")
                .build();

        suspensionRepository.save(suspension);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estasSuspendido(Long jugadorId, Long edicionId, Integer numeroFecha) {
        return suspensionRepository
                .existsByJugadorIdAndEdicionIdAndFechaSuspensionAndActivoTrue(
                        jugadorId, edicionId, numeroFecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuspensionResponse> listarPorJugador(Long jugadorId, Long edicionId) {
        return suspensionRepository
                .findByJugadorIdAndEdicionIdAndActivoTrue(jugadorId, edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuspensionResponse> listarPorEdicion(Long edicionId) {
        return suspensionRepository
                .findByEdicionIdAndActivoTrue(edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public SuspensionResponse levantar(Long suspensionId) {
        SuspensionJugador suspension = suspensionRepository.findById(suspensionId)
                .orElseThrow(() -> new ResourceNotFoundException("Suspensión no encontrada"));
        suspension.setActivo(false);
        suspension.setMotivo(suspension.getMotivo() + " [LEVANTADA POR ORGANIZADOR]");
        return toResponse(suspensionRepository.save(suspension));
    }

    private SuspensionResponse toResponse(SuspensionJugador s) {
        Jugador j = s.getJugador();
        return SuspensionResponse.builder()
                .id(s.getId())
                .jugador(JugadorResponse.builder()
                        .id(j.getId()).nombres(j.getNombres())
                        .apellidoPaterno(j.getApellidoPaterno())
                        .apellidoMaterno(j.getApellidoMaterno())
                        .tipoDocumento(j.getTipoDocumento())
                        .numeroDocumento(j.getNumeroDocumento())
                        .fechaNacimiento(j.getFechaNacimiento())
                        .nacionalidad(j.getNacionalidad())
                        .activo(j.getActivo()).build())
                .edicionId(s.getEdicion().getId())
                .partidoOrigenId(s.getPartidoOrigen().getId())
                .fechaOrigen(s.getFechaOrigen())
                .fechaSuspension(s.getFechaSuspension())
                .activo(s.getActivo())
                .motivo(s.getMotivo())
                .build();
    }
}
