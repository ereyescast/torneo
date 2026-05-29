package com.torneo.copaestudiantil.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Metadatos de paginación con cursores.
 * No hace COUNT(*) — solo sabe si hay más registros.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorMeta {
    private int pageSize;
    private int itemCount;        // registros en esta página
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}
