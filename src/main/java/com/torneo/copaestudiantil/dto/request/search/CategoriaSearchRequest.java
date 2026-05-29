package com.torneo.copaestudiantil.dto.request.search;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaSearchRequest {

    private Boolean activa;
    private Long organizadorId;
    private Long edicionId;
    private Integer anioNacimiento;
    private NivelCompetencia nivel;
    private ModalidadJuego modalidad;

    private CursorRequest pagination = new CursorRequest();
}
