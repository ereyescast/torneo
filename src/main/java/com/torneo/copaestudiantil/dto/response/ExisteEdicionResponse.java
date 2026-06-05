package com.torneo.copaestudiantil.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta del endpoint GET /api/ediciones/existe
 *
 * Si existe=true, incluye los datos de la edición existente
 * para que el front muestre al organizador cuál es y cuándo la creó:
 * "Ya tienes 'Copa Kids Enero 2026' que inicia el 07/06/2026,
 *  creada el 01/06/2026. Te llevamos a ella."
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExisteEdicionResponse {

    /** true si ya existe una edición con ese organizador + nombre + fechaInicio */
    private boolean existe;

    // ── Datos de la edición existente (null si existe=false) ──
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaCreacion;
}
