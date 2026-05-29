package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import com.torneo.copaestudiantil.entity.TipoDocumento;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TecnicoResponse implements HasId, HasSortValue {

    private Long id;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nacionalidad;
    private LocalDate fechaNac;
    private String profileImage;
    private Boolean activo;

    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "nombres"          -> nombres;
            case "apellidosPaterno" -> apellidosPaterno;
            case "numeroDocumento"  -> numeroDocumento;
            case "nacionalidad"     -> nacionalidad;
            case "fechaNac"         -> fechaNac != null ? fechaNac.toString() : null;
            default                 -> id;
        };
    }
}
