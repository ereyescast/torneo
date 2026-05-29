package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoResponse implements HasId, HasSortValue {

    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private CategoriaResponse categoria;
    private SedeResponse sede;
    private String nombre;
    private String logoUrl;
    private Boolean activo;

    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "nombre"        -> nombre;
            case "organizadorId" -> organizadorId;
            default              -> id;
        };
    }
}
