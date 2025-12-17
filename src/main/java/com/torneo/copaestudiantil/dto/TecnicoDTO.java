package com.torneo.copaestudiantil.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TecnicoDTO {
    private long id;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nacionalidad;
    private LocalDate fechaNac;
    private String fotoUrl;
}


