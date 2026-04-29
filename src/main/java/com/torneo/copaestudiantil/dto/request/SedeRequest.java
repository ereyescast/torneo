package com.torneo.copaestudiantil.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SedeRequest {
    private Long organizadorId;
    private String nombre;
    private String direccion;
    private Boolean activa;
}