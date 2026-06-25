package com.torneo.copaestudiantil.dto.request.search;

import com.torneo.copaestudiantil.entity.TipoDocumento;
import com.torneo.copaestudiantil.entity.PosicionJugador;
import lombok.*;

/**
 * Filtros dinámicos para búsqueda de jugadores.
 * Todos los campos son opcionales — solo se aplican si vienen con valor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JugadorSearchRequest {

    // Filtros
    private Boolean activo;
    private String nombres;               // LIKE %nombres%
    private String busqueda;              // busca en nombres O apellidos (OR)
    private String apellidoPaterno;       // LIKE %apellido%
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;       // exacto
    private String nacionalidad;
    private PosicionJugador posicion;     // filtra por posición exacta
    private Integer anioNacimientoDesde;  // fechaNacimiento >= YYYY-01-01
    private Integer anioNacimientoHasta;  // fechaNacimiento <= YYYY-12-31
    private Boolean tieneFoto;            // profileImage IS NOT NULL
    private Long edicionId;               // jugadores inscritos en esta edición
    private Long equipoId;                // jugadores inscritos en este equipo
    private Long categoriaId;             // jugadores inscritos en equipos de esta categoría

    // Paginación
    private CursorRequest pagination = new CursorRequest();
}
