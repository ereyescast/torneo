package com.torneo.copaestudiantil.entity;

/**
 * Estado de un pago registrado manualmente por el organizador.
 */
public enum EstadoPago {
    PENDIENTE,  // no ha pagado
    PAGADO,     // pagó — el organizador lo confirmó
    VENCIDO     // venció el plazo sin pagar (ej: hasta la 2da fecha)
}
