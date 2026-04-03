package com.torneo.copaestudiantil.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArbitroResponse {

    private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private Boolean activo;
}