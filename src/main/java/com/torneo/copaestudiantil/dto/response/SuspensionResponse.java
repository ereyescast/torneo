package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuspensionResponse {
    private Long id;
    private JugadorResponse jugador;
    private Long edicionId;
    private Long partidoOrigenId;
    private Integer fechaOrigen;
    private Integer fechaSuspension;
    private Boolean activo;
    private String motivo;
}
