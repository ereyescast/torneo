package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Item del ranking público de tarjetas.
 * Muestra el total de amarillas y rojas de un jugador en una edición.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TarjetaResponse {
    private Long jugadorId;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String equipoNombre;
    private Long totalAmarillas;
    private Long totalRojas;
}
