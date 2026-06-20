package com.torneo.copaestudiantil.dto.response;

import lombok.*;

/**
 * Item del DIRECTORIO PÚBLICO de torneos.
 * Para la pantalla donde un padre explora todos los torneos disponibles.
 * Incluye solo datos públicos no sensibles (nombre, slug, logo, ubicación).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TorneoDirectorioResponse {
    private Long id;
    private String nombre;
    private String codigoPublico;
    private String logoUrl;
    private String direccion;
}
