package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Datos públicos del organizador para la vista de padres.
 * NO incluye email, teléfono ni datos internos.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrganizadorPublicoResponse {
    private Long id;
    private String nombre;
    private String codigoPublico;
    private String logoUrl;
}
