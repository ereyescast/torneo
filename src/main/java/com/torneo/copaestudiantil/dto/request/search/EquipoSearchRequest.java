package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquipoSearchRequest {

    private Boolean activo;
    private String nombre;            // LIKE %nombre%
    private Long organizadorId;
    private Long edicionId;
    private Long categoriaId;
    private Long sedeId;

    private CursorRequest pagination = new CursorRequest();
}
