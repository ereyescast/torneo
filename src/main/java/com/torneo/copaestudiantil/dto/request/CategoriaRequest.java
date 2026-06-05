package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * organizadorId YA NO viene del body — sale del token en el service.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaRequest {
    @NotNull(message = "La edición es obligatoria")
    private Long edicionId;

    @NotNull(message = "El año de nacimiento es obligatorio")
    private Integer anioNacimiento;

    private NivelCompetencia nivel;
    private ModalidadJuego modalidad;
    private Integer maxJugadoresPorEquipo;
    private Boolean activa;
}
