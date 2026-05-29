package com.torneo.copaestudiantil.common.response;

/**
 * Interfaz que deben implementar los DTOs de response
 * para que CursorUtil pueda extraer el valor del campo de ordenamiento.
 *
 * Ejemplo en JugadorResponse:
 *   public Object getSortValue(String field) {
 *     return switch (field) {
 *       case "apellidoPaterno" -> apellidoPaterno;
 *       case "nombres"         -> nombres;
 *       case "numeroDocumento" -> numeroDocumento;
 *       default                -> id;
 *     };
 *   }
 */
public interface HasSortValue {
    Object getSortValue(String field);
}
