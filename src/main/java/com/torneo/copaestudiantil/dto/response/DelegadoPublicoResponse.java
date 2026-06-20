package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Delegado para la VISTA PÚBLICA del equipo. Expone solo el nombre.
 * No expone email ni datos de contacto.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DelegadoPublicoResponse {
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
}
