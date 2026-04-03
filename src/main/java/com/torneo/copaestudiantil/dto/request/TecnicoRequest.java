package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TecnicoRequest {

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidosPaterno;

    private String apellidosMaterno;

    @NotBlank
    private String tipoDocumento;

    @NotBlank
    private String numeroDocumento;

    private String nacionalidad;

    private LocalDate fechaNac;
}