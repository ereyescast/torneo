package com.torneo.copaestudiantil.dto.request.search;

import lombok.*;
import java.time.LocalDate;

/**
 * organizadorId YA NO viene del body — el service fuerza el filtro por el token.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EdicionSearchRequest {
    private Boolean activa;
    private String nombre;
    private LocalDate fechaInicioDesdE;
    private LocalDate fechaInicioHasta;
    private CursorRequest pagination = new CursorRequest();
}
