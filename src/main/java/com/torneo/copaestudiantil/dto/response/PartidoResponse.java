package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartidoResponse {
    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private CategoriaResponse categoria;
    private SedeResponse sede;
    private EquipoResponse equipoLocal;
    private EquipoResponse equipoVisitante;
    private LocalDateTime fechaHora;
    private Integer golesLocal;
    private Integer golesVisitante;
    private EstadoPartido estado;
    private FasePartido fase;
    private Long grupoId;
    private Boolean activo;
}