package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.Genero;
import com.torneo.copaestudiantil.entity.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

/**
 * organizadorId YA NO viene del body — sale del token en el service.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JugadorRequest {
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    private String apellidoMaterno;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    private String nacionalidad;

    /** Género — necesario para la regla Art. 22 (niña juega 1 categoría arriba). */
    private Genero genero;

    /** Posición en cancha. Opcional. */
    private com.torneo.copaestudiantil.entity.PosicionJugador posicion;

    private Boolean activo;
}
