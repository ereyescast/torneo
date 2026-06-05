package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * El organizadorId YA NO viene en el request.
 * Se obtiene del usuario autenticado (token JWT) en el service.
 * Esto cierra el hueco donde un usuario podía crear sedes para otro organizador.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SedeRequest {

    @NotBlank(message = "El nombre de la sede es obligatorio")
    private String nombre;

    private String direccion;

    private Boolean activa;
}
