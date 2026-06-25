package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/** Contexto del delegado autenticado: su equipo y la edición a la que pertenece. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MiEquipoDelegadoResponse {
    private Long equipoId;
    private String equipoNombre;
    private Long edicionId;
    private String edicionNombre;
    private Long categoriaId;
    // Datos de la categoría del equipo, para que el delegado vea su contexto
    // y se valide la edad de los jugadores que inscribe.
    private Integer categoriaAnioNacimiento;
    private String categoriaNivel;
    private String categoriaModalidad;
}
