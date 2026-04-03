package com.torneo.copaestudiantil.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TecnicoResponse {

    private Long id;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nacionalidad;
    private LocalDate fechaNac;
    private String profileImage;
}