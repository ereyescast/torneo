package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;

/**
 * El organizadorId YA NO viene en el request de búsqueda.
 * El service lo fuerza desde el token, de modo que cada organizador
 * SOLO puede buscar dentro de sus propias sedes.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SedeSearchRequest {
    private Boolean activa;
    private String nombre;        // LIKE %nombre%
    private String direccion;     // LIKE %direccion%
    private CursorRequest pagination = new CursorRequest();
}
