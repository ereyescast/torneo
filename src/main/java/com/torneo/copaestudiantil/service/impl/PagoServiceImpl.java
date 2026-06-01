package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.PagoEquipoRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoEquipoRepository  pagoRepository;
    private final EquipoRepository      equipoRepository;
    private final EdicionTorneoRepository edicionRepository;
    private final PartidoRepository     partidoRepository;

    @Override
    public PagoEquipoResponse registrar(PagoEquipoRequest request) {
        Equipo equipo = equipoRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        Partido partido = null;
        if (request.getPartidoId() != null) {
            partido = partidoRepository.findById(request.getPartidoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        }

        PagoEquipo pago = PagoEquipo.builder()
                .organizadorId(request.getOrganizadorId())
                .equipo(equipo)
                .edicion(edicion)
                .tipoPago(request.getTipoPago())
                .estado(EstadoPago.PENDIENTE)
                .monto(request.getMonto())
                .fechaLimite(request.getFechaLimite())
                .referenciaPago(request.getReferenciaPago())
                .observacion(request.getObservacion())
                .partido(partido)
                .build();

        return toResponse(pagoRepository.save(pago));
    }

    @Override
    public PagoEquipoResponse confirmarPago(Long pagoId, String referenciaPago) {
        PagoEquipo pago = findById(pagoId);
        if (EstadoPago.PAGADO.equals(pago.getEstado()))
            throw new BadRequestException("Este pago ya fue confirmado anteriormente");
        pago.setEstado(EstadoPago.PAGADO);
        pago.setFechaPago(LocalDate.now());
        if (referenciaPago != null && !referenciaPago.isBlank())
            pago.setReferenciaPago(referenciaPago);
        return toResponse(pagoRepository.save(pago));
    }

    @Override
    public PagoEquipoResponse marcarVencido(Long pagoId) {
        PagoEquipo pago = findById(pagoId);
        if (EstadoPago.PAGADO.equals(pago.getEstado()))
            throw new BadRequestException("No se puede marcar como vencido un pago ya confirmado");
        pago.setEstado(EstadoPago.VENCIDO);
        return toResponse(pagoRepository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoEquipoResponse> listarPorEquipo(Long equipoId, Long edicionId) {
        return pagoRepository.findByEquipoIdAndEdicionId(equipoId, edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoEquipoResponse> listarPorEdicion(Long edicionId) {
        return pagoRepository.findByEdicionId(edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoEquipoResponse> listarDeudores(Long edicionId) {
        return pagoRepository.findByEdicionIdAndEstadoIn(edicionId,
                List.of(EstadoPago.PENDIENTE, EstadoPago.VENCIDO))
                .stream().map(this::toResponse).toList();
    }

    @Override
    public void generarMultaWo(Partido partido, Long equipoWoId) {
        Equipo equipo = equipoWoId.equals(partido.getEquipoLocal().getId())
                ? partido.getEquipoLocal()
                : partido.getEquipoVisitante();

        PagoEquipo multa = PagoEquipo.builder()
                .organizadorId(partido.getOrganizadorId())
                .equipo(equipo)
                .edicion(partido.getEdicion())
                .tipoPago(TipoPago.MULTA_WO)
                .estado(EstadoPago.PENDIENTE)
                .monto(new BigDecimal("50.00"))
                .observacion("Multa por WO en partido id=" + partido.getId()
                        + " — Art. 16a. Debe pagarse antes del próximo partido.")
                .partido(partido)
                .build();

        pagoRepository.save(multa);
    }

    @Override
    public PagoEquipoResponse inicializarInscripcion(Long equipoId, Long edicionId,
                                                      Long organizadorId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        EdicionTorneo edicion = edicionRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        // Monto según modalidad de la categoría del equipo
        BigDecimal monto = BigDecimal.valueOf(
                equipo.getCategoria().getModalidad().getCostoInscripcionSoles());

        PagoEquipo pago = PagoEquipo.builder()
                .organizadorId(organizadorId)
                .equipo(equipo)
                .edicion(edicion)
                .tipoPago(TipoPago.INSCRIPCION)
                .estado(EstadoPago.PENDIENTE)
                .monto(monto)
                .observacion("Inscripción " + equipo.getCategoria().getModalidad()
                        + " — " + equipo.getNombre())
                .build();

        return toResponse(pagoRepository.save(pago));
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private PagoEquipo findById(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
    }

    private PagoEquipoResponse toResponse(PagoEquipo p) {
        Equipo eq = p.getEquipo();
        EdicionTorneo ed = p.getEdicion();

        return PagoEquipoResponse.builder()
                .id(p.getId())
                .organizadorId(p.getOrganizadorId())
                .equipo(EquipoResponse.builder()
                        .id(eq.getId()).nombre(eq.getNombre())
                        .organizadorId(eq.getOrganizadorId())
                        .activo(eq.getActivo()).build())
                .edicion(EdicionTorneoResponse.builder()
                        .id(ed.getId()).nombre(ed.getNombre())
                        .fechaInicio(ed.getFechaInicio()).fechaFin(ed.getFechaFin())
                        .activa(ed.getActiva()).build())
                .tipoPago(p.getTipoPago())
                .estado(p.getEstado())
                .monto(p.getMonto())
                .fechaLimite(p.getFechaLimite())
                .fechaPago(p.getFechaPago())
                .referenciaPago(p.getReferenciaPago())
                .observacion(p.getObservacion())
                .partidoId(p.getPartido() != null ? p.getPartido().getId() : null)
                .build();
    }
}
