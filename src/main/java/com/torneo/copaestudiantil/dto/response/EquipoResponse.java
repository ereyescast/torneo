package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoResponse {
    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private CategoriaResponse categoria;
    private SedeResponse sede;
    private String nombre;
    private String logoUrl;
    private Boolean activo;
}