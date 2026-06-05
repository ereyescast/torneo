package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * organizadorId YA NO viene del body — sale del token en el service.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquipoRequest {
    @NotNull(message = "La edición es obligatoria")
    private Long edicionId;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    private String logoUrl;
    private Boolean activo;
}
