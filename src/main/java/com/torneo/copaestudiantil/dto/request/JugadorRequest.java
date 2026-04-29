package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.TipoDocumento;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JugadorRequest {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
}