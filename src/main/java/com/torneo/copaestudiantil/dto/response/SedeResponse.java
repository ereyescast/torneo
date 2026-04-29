package com.torneo.copaestudiantil.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SedeResponse {
    private Long id;
    private Long organizadorId;
    private String nombre;
    private String direccion;
    private Boolean activa;
}