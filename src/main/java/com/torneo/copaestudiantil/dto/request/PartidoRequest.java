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

    /**
     * Fase del torneo. Default: GRUPOS si no se envía.
     */
    private FasePartido fase;

    /**
     * Obligatorio cuando fase = GRUPOS. Null en fases eliminatorias.
     */
    private Long grupoId;
}
