package com.torneo.copaestudiantil.dto.request;

import lombok.*;
import java.time.LocalDate;

/** organizadorId sale del token en el service. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FixtureRequest {
    private Long edicionId;
    private Long categoriaId;
    private Long sedeId;
    private LocalDate fechaTorneo;
    private Integer numeroFecha;
}
