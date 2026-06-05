package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

/**
 * organizadorId YA NO viene del body — sale del token en el service.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EdicionTorneoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    private Boolean activa;
}
