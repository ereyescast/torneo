package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.common.response.HasId;
import com.torneo.copaestudiantil.common.response.HasSortValue;
import com.torneo.copaestudiantil.entity.TipoDocumento;
import com.torneo.copaestudiantil.entity.PosicionJugador;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JugadorResponse implements HasId, HasSortValue {

    private Long id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private PosicionJugador posicion;
    private String profileImage;
    private Boolean consentimientoFoto;
    private Boolean activo;

    /**
     * Retorna el valor del campo solicitado para usar en el cursor.
     * Permite ordenar por cualquier campo y generar cursores precisos.
     */
    @Override
    public Object getSortValue(String field) {
        return switch (field) {
            case "nombres"          -> nombres;
            case "apellidoPaterno"  -> apellidoPaterno;
            case "apellidoMaterno"  -> apellidoMaterno;
            case "numeroDocumento"  -> numeroDocumento;
            case "fechaNacimiento"  -> fechaNacimiento != null
                    ? fechaNacimiento.toString() : null;
            case "nacionalidad"     -> nacionalidad;
            default                 -> id;
        };
    }
}
