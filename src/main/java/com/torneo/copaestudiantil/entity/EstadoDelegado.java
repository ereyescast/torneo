package com.torneo.copaestudiantil.entity;

/**
 * Estado de un delegado dentro del flujo de invitación.
 *  - PENDIENTE: el organizador generó el código pero el delegado aún no se registró.
 *  - ACTIVO:    el delegado ya creó su cuenta con el código.
 */
public enum EstadoDelegado {
    PENDIENTE,
    ACTIVO
}
