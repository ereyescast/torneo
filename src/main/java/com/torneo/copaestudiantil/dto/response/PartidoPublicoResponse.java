package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Partido para la VISTA PÚBLICA (padres).
 * Incluye el nombre del grupo y el nombre de la sede donde se juega.
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
    private String grupoNombre;
    private String sedeNombre;
    private String cancha;
}
