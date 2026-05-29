package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EdicionSearchRequest {

    private Boolean activa;
    private String nombre;             // LIKE %nombre%
    private Long organizadorId;
    private LocalDate fechaInicioDesdE;
    private LocalDate fechaInicioHasta;

    private CursorRequest pagination = new CursorRequest();
}
