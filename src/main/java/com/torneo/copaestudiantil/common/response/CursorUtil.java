package com.torneo.copaestudiantil.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torneo.copaestudiantil.common.trace.TraceContext;
import lombok.experimental.UtilityClass;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para codificar y decodificar cursores enriquecidos.
 *
 * El cursor codifica:
 *   - id        → posición del último registro
 *   - sortField → campo por el que se ordenó
 *   - sortValue → valor del sortField en el último registro (para desempate)
 *   - orgId     → ID del usuario autenticado que generó el cursor (seguridad)
 *   - v         → versión del cursor (compatibilidad futura)
 *
 * Seguridad: al decodificar se valida que el orgId del cursor
 * coincida con el usuario autenticado actual. Si no coincide,
 * se rechaza el cursor — evita data leak entre organizadores.






 */
@UtilityClass
public class CursorUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CURSOR_VERSION = "1";

    // ── Codificación ─────────────────────────────────────────────────────────




    public static String encode(Long id, String sortField, Object sortValue) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("id", id);
            payload.put("sortField", sortField != null ? sortField : "id");
            payload.put("sortValue", sortValue != null ? sortValue.toString() : id.toString());
            payload.put("orgId", TraceContext.getOrganizadorId());
            payload.put("v", CURSOR_VERSION);

            String json = MAPPER.writeValueAsString(payload);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(json.getBytes());
        } catch (Exception e) {
            return null;
        }
    }




    public static String encode(Long id) {
        return encode(id, "id", id);
    }

    // ── Decodificación ───────────────────────────────────────────────────────

    /**
     * Decodifica y valida el cursor.
     *
     * Retorna null si:
     *   - El cursor es inválido o está malformado
     *   - El orgId del cursor NO coincide con el usuario autenticado actual
     *     (posible intento de acceder a datos de otro organizador)
     */
    public static CursorPayload decode(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            Map<?, ?> map = MAPPER.readValue(decoded, Map.class);

            CursorPayload payload = new CursorPayload();
            payload.setId(toLong(map.get("id")));
            payload.setSortField(toString(map.get("sortField")));
            payload.setSortValue(toString(map.get("sortValue")));
            payload.setOrgId(toLong(map.get("orgId")));
            payload.setVersion(toString(map.get("v")));

            // ── Validación de seguridad ───────────────────────────────────
            // Si el usuario está autenticado (orgId en TraceContext),
            // verificar que el cursor pertenece al mismo usuario.
            Long currentOrgId = TraceContext.getOrganizadorId();
            if (currentOrgId != null
                    && payload.getOrgId() != null
                    && !currentOrgId.equals(payload.getOrgId())) {
                // Cursor de otro organizador — rechazar silenciosamente
                // No lanzar excepción para no revelar que el cursor existía
                return null;
            }

            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decodifica solo el ID del cursor (uso rápido en Specifications).
     * Incluye la validación de orgId.
     */
    public static Long decodeId(String cursor) {
        CursorPayload payload = decode(cursor);
        return payload != null ? payload.getId() : null;
    }

    // ── Builder de CursorData ────────────────────────────────────────────────













    public static <T extends HasId & HasSortValue> CursorData<T> build(
            List<T> items,
            int limit,
            String sortField,
            String previousCursor) {

        boolean hasNext = items.size() > limit;
        List<T> pageItems = hasNext ? items.subList(0, limit) : items;

        String nextCursor = null;
        if (hasNext) {
            T last = pageItems.get(pageItems.size() - 1);
            nextCursor = encode(last.getId(), sortField, last.getSortValue(sortField));
        }

        CursorMeta meta = CursorMeta.builder()
                .pageSize(limit)
                .itemCount(pageItems.size())
                .hasNextPage(hasNext)
                .hasPreviousPage(previousCursor != null)
                .build();

        CursorData.CursorInfo cursors = CursorData.CursorInfo.builder()
                .nextCursor(nextCursor)
                .previousCursor(previousCursor)
                .build();

        return CursorData.<T>builder()
                .items(pageItems)
                .meta(meta)
                .cursors(cursors)
                .build();
    }




    public static <T extends HasId> CursorData<T> build(
            List<T> items,
            int limit,
            String previousCursor) {

        boolean hasNext = items.size() > limit;
        List<T> pageItems = hasNext ? items.subList(0, limit) : items;

        String nextCursor = null;
        if (hasNext) {
            T last = pageItems.get(pageItems.size() - 1);
            nextCursor = encode(last.getId(), "id", last.getId());
        }

        CursorMeta meta = CursorMeta.builder()
                .pageSize(limit)
                .itemCount(pageItems.size())
                .hasNextPage(hasNext)
                .hasPreviousPage(previousCursor != null)
                .build();

        CursorData.CursorInfo cursors = CursorData.CursorInfo.builder()
                .nextCursor(nextCursor)
                .previousCursor(previousCursor)
                .build();

        return CursorData.<T>builder()
                .items(pageItems)
                .meta(meta)
                .cursors(cursors)
                .build();
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    private static Long toLong(Object val) {
        if (val instanceof Number) return ((Number) val).longValue();
        if (val instanceof String) {
            try { return Long.parseLong((String) val); } catch (Exception e) { return null; }
        }
        return null;
    }

    private static String toString(Object val) {
        return val != null ? val.toString() : null;
    }

    // ── Clases internas ──────────────────────────────────────────────────────

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    public static class CursorPayload {
        private Long id;
        private String sortField;
        private String sortValue;
        private Long orgId;
        private String version;
    }
}
