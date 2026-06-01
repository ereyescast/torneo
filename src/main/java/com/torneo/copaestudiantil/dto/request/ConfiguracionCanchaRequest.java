package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.ModalidadJuego;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracionCanchaRequest {

    @NotNull(message = "El nombre de la cancha es obligatorio")
    private String nombreCancha;      // "Campo 1", "Campo 7"

    @NotNull(message = "La modalidad es obligatoria")
    private ModalidadJuego modalidad; // FUTBOL_7, FUTBOL_9

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;     // 08:00

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;        // 13:00

    /**
     * Duración del partido en minutos.
     * Si no se envía, se usa el default del enum ModalidadJuego.
     * ART. 27: F7 = 40 min, F9 = 40 min
     */
    private Integer duracionPartidoMin;
}
