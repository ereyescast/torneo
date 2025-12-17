package com.torneo.copaestudiantil.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TecnicoRequest {
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nacionalidad;
    private LocalDate fechaNac;
}