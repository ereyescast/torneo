package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRequest {
    private Long organizadorId;
    private Long edicionId;
    private Integer anioNacimiento;
    private NivelCompetencia nivel;
    private ModalidadJuego modalidad;
    private Integer maxJugadoresPorEquipo;
    private Boolean activa;
}