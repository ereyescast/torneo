package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartidoRequest {

    @NotNull(message = "El organizadorId es obligatorio")
    private Long organizadorId;

    @NotNull(message = "La edición es obligatoria")
    private Long edicionId;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;

    @NotNull(message = "El equipo local es obligatorio")
    private Long equipoLocalId;

    @NotNull(message = "El equipo visitante es obligatorio")
    private Long equipoVisitanteId;

    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fechaHora;

    private EstadoPartido estado;
    private FasePartido fase;
    private Long grupoId;

    /** Cancha donde se juega. Ej: "Campo 1", "Campo 7" */
    private String cancha;

    /** Fixture al que pertenece. Null si es partido manual. */
    private Long fixtureId;
}
