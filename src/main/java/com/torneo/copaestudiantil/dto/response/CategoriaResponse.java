package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaResponse implements HasId, HasSortValue {

    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private Integer anioNacimiento;
    private NivelCompetencia nivel;
    private ModalidadJuego modalidad;
    private Integer maxJugadoresPorEquipo;
    private Boolean activa;

    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "anioNacimiento" -> anioNacimiento;
            case "nivel"          -> nivel != null ? nivel.name() : null;
            case "modalidad"      -> modalidad != null ? modalidad.name() : null;
            default               -> id;
        };
    }
}
