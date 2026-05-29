package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SedeResponse implements HasId, HasSortValue {

    private Long id;
    private Long organizadorId;
    private String nombre;
    private String direccion;
    private Boolean activa;

    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "nombre"    -> nombre;
            case "direccion" -> direccion;
            default          -> id;
        };
    }
}
