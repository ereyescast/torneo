package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartidoRequest {
    private Long organizadorId;
    private Long edicionId;
    private Long categoriaId;
    private Long sedeId;
    private Long equipoLocalId;
    private Long equipoVisitanteId;
    private LocalDateTime fechaHora;
    private EstadoPartido estado;

    /**
     * Fase del torneo. Default: GRUPOS si no se envía.
     */
    private FasePartido fase;

    /**
     * ID del grupo al que pertenece el partido.
     * Obligatorio cuando fase = GRUPOS; null en fases eliminatorias.
     */
    private Long grupoId;
}