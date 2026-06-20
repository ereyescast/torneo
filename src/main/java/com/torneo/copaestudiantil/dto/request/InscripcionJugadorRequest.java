package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * organizadorId YA NO viene del body — sale del token en el service.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InscripcionJugadorRequest {
    @NotNull(message = "El jugador es obligatorio")
    private Long jugadorId;

    @NotNull(message = "El equipo es obligatorio")
    private Long equipoId;

    @NotNull(message = "La edición es obligatoria")
    private Long edicionId;
}
