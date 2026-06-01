package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.PagoEquipoRequest;
import com.torneo.copaestudiantil.dto.response.PagoEquipoResponse;
import com.torneo.copaestudiantil.entity.Partido;

import java.util.List;

public interface PagoService {

    /** Registra un pago (inscripción, arbitraje, multa) */
    PagoEquipoResponse registrar(PagoEquipoRequest request);

    /** Confirma un pago como PAGADO */
    PagoEquipoResponse confirmarPago(Long pagoId, String referenciaPago);

    /** Marca un pago como VENCIDO */
    PagoEquipoResponse marcarVencido(Long pagoId);

    /** Lista pagos de un equipo en una edición */
    List<PagoEquipoResponse> listarPorEquipo(Long equipoId, Long edicionId);

    /** Lista todos los pagos de una edición */
    List<PagoEquipoResponse> listarPorEdicion(Long edicionId);

    /** Lista equipos con deuda pendiente en una edición */
    List<PagoEquipoResponse> listarDeudores(Long edicionId);

    /**
     * Crea automáticamente la multa por WO (Art. 16a — S/.50).
     * Llamado desde PartidoServiceImpl.registrarWo()
     */
    void generarMultaWo(Partido partido, Long equipoWoId);

    /**
     * Inicializa el pago de inscripción cuando se registra un equipo.
     * Estado inicial: PENDIENTE
     */
    PagoEquipoResponse inicializarInscripcion(Long equipoId, Long edicionId, Long organizadorId);
}
