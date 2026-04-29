package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponse {
    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private Integer anioNacimiento;
    private NivelCompetencia nivel;
    private ModalidadJuego modalidad;
    private Integer maxJugadoresPorEquipo;
    private Boolean activa;
}