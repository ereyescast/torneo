package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GrupoResponse {
    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private CategoriaResponse categoria;
    private String nombre;
    private Boolean activo;

    /** Cantidad de equipos activos en el grupo (para mostrar "X/Y"). */
    private Long cantidadEquipos;
}
