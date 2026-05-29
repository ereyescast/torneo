package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ArbitroSearchRequest {

    private Boolean activo;
    private String nombre;        // PREFIJO — LIKE 'valor%'
    private String email;         // EXACTO
    private Long organizadorId;

    private CursorRequest pagination = new CursorRequest();
}
