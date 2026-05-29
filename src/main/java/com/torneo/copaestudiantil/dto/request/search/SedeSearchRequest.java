package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SedeSearchRequest {

    private Boolean activa;
    private String nombre;        // LIKE %nombre%
    private String direccion;     // LIKE %direccion%
    private Long organizadorId;

    private CursorRequest pagination = new CursorRequest();
}
