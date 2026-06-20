package com.torneo.copaestudiantil.dto.response;

import lombok.*;

import java.util.List;

/**
 * Respuesta del endpoint público de plantel.
 * Modela "el plantel de un equipo" = su técnico (puede ser null) + sus jugadores.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlantelResponse {
    /** Técnico activo del equipo. Puede ser null si no tiene asignado. */
    private TecnicoPublicoResponse tecnico;

    /** Delegado del equipo. Puede ser null si no tiene registrado. */
    private DelegadoPublicoResponse delegado;

    @Builder.Default
    private List<JugadorPublicoResponse> jugadores = List.of();
}
