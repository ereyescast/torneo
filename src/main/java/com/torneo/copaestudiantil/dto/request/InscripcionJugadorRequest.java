package com.torneo.copaestudiantil.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InscripcionJugadorRequest {
    private Long organizadorId;
    private Long jugadorId;
    private Long equipoId;
    private Long edicionId;
}