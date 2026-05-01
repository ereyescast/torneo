package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GrupoEquipoResponse {
    private Long id;
    private GrupoResponse grupo;
    private EquipoResponse equipo;
    private Boolean activo;
}
