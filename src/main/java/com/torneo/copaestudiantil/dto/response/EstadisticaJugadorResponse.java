package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadisticaJugadorResponse {
    private Long id;
    private Long organizadorId;
    private JugadorResponse jugador;
    private Long partidoId;
    private EquipoResponse equipo;
    private EdicionTorneoResponse edicion;
    private Integer goles;
    private Integer asistencias;
    private Integer tarjetasAmarillas;
    private Integer tarjetasRojas;
    private Integer minutosJugados;
    private Boolean titular;
}
