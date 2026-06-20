package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import lombok.*;
import java.time.LocalDateTime;

/** organizadorId sale del token en el service. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartidoRequest {
    private Long edicionId;
    private Long categoriaId;
    private Long sedeId;
    private Long equipoLocalId;
    private Long equipoVisitanteId;
    private LocalDateTime fechaHora;
    private EstadoPartido estado;
    private FasePartido fase;
    private Long grupoId;
    private String cancha;
    private Long fixtureId;
}
