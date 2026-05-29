package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.TipoDocumento;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TecnicoRequest {

    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nacionalidad;
    private LocalDate fechaNac;
}
