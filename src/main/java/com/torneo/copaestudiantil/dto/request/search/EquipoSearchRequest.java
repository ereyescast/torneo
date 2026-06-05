package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;

/**
 * organizadorId YA NO viene del body — el service fuerza el filtro por el token.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquipoSearchRequest {
    private Boolean activo;
    private String nombre;        // LIKE %nombre%
    private Long edicionId;
    private Long categoriaId;
    private Long sedeId;
    private CursorRequest pagination = new CursorRequest();
}
