package com.torneo.copaestudiantil.dto.response;

import lombok.*;

import java.util.List;

/**
 * Historial completo de un equipo en el torneo.
 * Art. 14 — endpoint GET /api/equipos/{id}/historial
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialEquipoResponse {

    private EquipoResponse equipo;

    /** Partidos de la edición consultada o de todas las ediciones */
    private List<PartidoResponse> partidos;

    /** Resumen estadístico */
    private ResumenEstadistico resumen;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ResumenEstadistico {
        private Integer totalPartidos;
        private Integer victorias;
        private Integer empates;
        private Integer derrotas;
        private Integer golesFavor;
        private Integer golesContra;
        private Integer diferenciaGol;
        private Integer puntos;
        private Integer wos;
    }
}
