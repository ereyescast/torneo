package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TablaPosicionResponse {

    private Long id;
    private Long equipoId;
    private String equipoNombre;
    private Long edicionId;
    private Long categoriaId;
    private Long grupoId;
    private String grupoNombre;
    private Integer partidosJugados;
    private Integer partidosGanados;
    private Integer partidosEmpatados;
    private Integer partidosPerdidos;
    private Integer golesFavor;
    private Integer golesContra;
    private Integer diferenciaGol;
    private Integer puntos;
}
