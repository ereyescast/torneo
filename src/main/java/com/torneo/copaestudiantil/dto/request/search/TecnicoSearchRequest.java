package com.torneo.copaestudiantil.dto.request.search;

import com.torneo.copaestudiantil.entity.TipoDocumento;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TecnicoSearchRequest {

    // Filtros — todos opcionales
    private Boolean activo;
    private String nombres;           // LIKE %nombres%
    private String apellidosPaterno;  // LIKE %apellido%
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;   // exacto
    private String nacionalidad;      // LIKE %nacionalidad%
    private Long edicionId;           // técnicos asignados a esta edición
    private Long equipoId;            // técnicos asignados a este equipo

    // Paginación con cursor
    private CursorRequest pagination = new CursorRequest();
}
