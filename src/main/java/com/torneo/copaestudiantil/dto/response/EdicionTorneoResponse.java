package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EdicionTorneoResponse implements HasId, HasSortValue {
    private Long id;
    private Long organizadorId;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activa;

    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "nombre"      -> nombre;
            case "fechaInicio" -> fechaInicio != null ? fechaInicio.toString() : null;
            case "fechaFin"    -> fechaFin != null ? fechaFin.toString() : null;
            default            -> id;
        };
    }
}
