package com.torneo.copaestudiantil.dto.response;

import lombok.*;
import java.time.LocalDate;

/** Vista ligera de edición para la capa pública y para anidar. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EdicionResumenResponse {
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activa;
}
