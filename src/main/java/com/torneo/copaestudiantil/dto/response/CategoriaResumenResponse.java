package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import lombok.*;

/** Vista ligera de categoría para la capa pública y para anidar. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaResumenResponse {
    private Long id;
    private Integer anioNacimiento;
    private NivelCompetencia nivel;
    private ModalidadJuego modalidad;
    private Boolean activa;
}
