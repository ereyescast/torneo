package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Sede para la VISTA PÚBLICA: dónde se juega el torneo.
 * Es lo que el padre necesita saber para llegar al campo.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SedePublicaResponse {
    private Long id;
    private String nombre;
    private String direccion;
}
