package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;

/**
 * Paginación con cursores.
 * El frontend manda el nextCursor que recibió en la respuesta anterior.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursorRequest {

    private int limit = 20;           // registros por página (default 20, max 100)
    private String nextCursor;        // cursor para la siguiente página (null = primera página)
    private String previousCursor;    // cursor para la página anterior
    private String sortBy = "id";     // campo de ordenamiento
    private String direction = "ASC"; // ASC o DESC

    public int getLimit() {
        // Proteger contra límites abusivos
        if (limit <= 0) return 20;
        if (limit > 100) return 100;
        return limit;
    }
}
