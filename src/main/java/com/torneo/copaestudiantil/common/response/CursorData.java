package com.torneo.copaestudiantil.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * Contenedor de datos paginados con cursores.
 * El frontend usa nextCursor para pedir la siguiente página.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorData<T> {

    private List<T> items;
    private CursorMeta meta;
    private CursorInfo cursors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CursorInfo {
        private String nextCursor;
        private String previousCursor;
    }
}
