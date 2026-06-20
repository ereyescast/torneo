package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Item del ranking público de goleadores.
 * Incluye asistencias como plus diferenciador (FutPlay no las muestra).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GoleadorResponse {
    private Long jugadorId;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String equipoNombre;
    private Long totalGoles;
    private Long totalAsistencias;
}
