package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Partido para la VISTA PÚBLICA (padres).
 * Versión ligera: solo lo que el público necesita ver, con equipos como resumen.
 * No expone datos internos de gestión.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartidoPublicoResponse {
    private Long id;
    private LocalDateTime fechaHora;
    private EquipoResumenResponse equipoLocal;
    private EquipoResumenResponse equipoVisitante;
    private Integer golesLocal;
    private Integer golesVisitante;
    private EstadoPartido estado;
    private FasePartido fase;
    private Long grupoId;
    private String cancha;
}
