package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerarFixtureRequest {

    @NotNull(message = "El organizadorId es obligatorio")
    private Long organizadorId;

    /**
     * Si se envía, genera solo los partidos de ese grupo.
     * Si es null, genera todos los grupos de la categoría del fixture.
     */
    private Long grupoId;
}
