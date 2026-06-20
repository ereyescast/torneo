package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Técnico para la VISTA PÚBLICA. Expone solo el nombre y la foto (opcional).
 * NO expone número de documento ni fecha de nacimiento, aunque sea adulto:
 * no hay razón para publicar el DNI de nadie en una vista abierta.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TecnicoPublicoResponse {
    private Long id;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String profileImage;
}
