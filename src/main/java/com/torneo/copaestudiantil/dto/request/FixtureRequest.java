package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FixtureRequest {

    @NotNull(message = "El organizadorId es obligatorio")
    private Long organizadorId;

    @NotNull(message = "La edición es obligatoria")
    private Long edicionId;

    /** null = fixture para todas las categorías del día */
    private Long categoriaId;

    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;

    @NotNull(message = "La fecha del torneo es obligatoria")
    private LocalDate fechaTorneo;

    @NotNull(message = "El número de fecha es obligatorio")
    private Integer numeroFecha;
}
