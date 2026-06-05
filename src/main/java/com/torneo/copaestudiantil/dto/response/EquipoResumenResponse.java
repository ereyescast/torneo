package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/** Vista ligera de equipo para la capa pública y para anidar. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquipoResumenResponse {
    private Long id;
    private Long organizadorId;
    private String nombre;
    private String logoUrl;
    private Boolean activo;
}
