package com.torneo.copaestudiantil.common.response;

/**
 * Interfaz que deben implementar los DTOs de response
 * para que CursorUtil pueda extraer el ID y generar el cursor.
 */
public interface HasId {
    Long getId();
}
