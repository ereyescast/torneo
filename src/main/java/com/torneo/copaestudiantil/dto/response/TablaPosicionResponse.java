package com.torneo.copaestudiantil.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TablaPosicionResponse {

    private Long equipoId;
    private String nombreEquipo;

    /** ID del grupo (null si la tabla es global) */
    private Long grupoId;

    /** Nombre del grupo, ej: "A", "B" (null si la tabla es global) */
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