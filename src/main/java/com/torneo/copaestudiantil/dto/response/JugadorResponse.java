package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.TipoDocumento;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JugadorResponse {
    private Long id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String profileImage;
    private Boolean activo;
}