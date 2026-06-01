package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import lombok.*;

import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracionCanchaResponse {

    private Long id;
    private String nombreCancha;
    private ModalidadJuego modalidad;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer duracionPartidoMin;
    private Integer capacidadPartidos; // calculado: (horaFin - horaInicio) / duracion
}
