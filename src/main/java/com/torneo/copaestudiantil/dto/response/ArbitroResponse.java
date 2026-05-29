package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArbitroResponse implements HasId, HasSortValue {

    private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private Boolean activo;

    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "nombre"  -> nombre;
            case "email"   -> email;
            default        -> id;
        };
    }
}
