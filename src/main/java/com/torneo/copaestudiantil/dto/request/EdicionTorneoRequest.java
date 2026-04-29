package com.torneo.copaestudiantil.dto.request;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EdicionTorneoRequest {
    private Long organizadorId;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activa;
}