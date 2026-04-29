package com.torneo.copaestudiantil.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InscripcionJugadorResponse {
    private Long id;
    private JugadorResponse jugador;
    private EquipoResponse equipo;
    private EdicionTorneoResponse edicion;
    private Boolean activo;
    private LocalDateTime fechaInscripcion;
}