package com.torneo.copaestudiantil.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquipoRequest {
    private Long organizadorId;
    private Long edicionId;
    private Long categoriaId;
    private Long sedeId;
    private String nombre;
    private String logoUrl;
    private Boolean activo;
}