package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.SedeSearchRequest;
import com.torneo.copaestudiantil.entity.Sede;
import org.springframework.data.jpa.domain.Specification;

public class SedeSpecification {

    public static Specification<Sede> fromRequest(SedeSearchRequest req) {
        return Specification
                .where(conActiva(req.getActiva()))
                .and(conNombre(req.getNombre()))
                .and(conDireccion(req.getDireccion()))
                .and(desdeCursor(req.getPagination() != null
                        ? req.getPagination().getNextCursor() : null));
    }

    public static Specification<Sede> conActiva(Boolean activa) {
        return (root, query, cb) ->
                activa == null ? null : cb.equal(root.get("activa"), activa);
    }

    public static Specification<Sede> conNombre(String nombre) {
        return (root, query, cb) ->
                nombre == null || nombre.isBlank() ? null
                        : cb.like(cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase().trim() + "%");
    }

    public static Specification<Sede> conDireccion(String direccion) {
        return (root, query, cb) ->
                direccion == null || direccion.isBlank() ? null
                        : cb.like(cb.lower(root.get("direccion")),
                        "%" + direccion.toLowerCase().trim() + "%");
    }

    public static Specification<Sede> desdeCursor(String cursor) {
        return (root, query, cb) -> {
            if (cursor == null || cursor.isBlank()) return null;

            CursorUtil.CursorPayload payload = CursorUtil.decode(cursor);
            if (payload == null) return null;

            Long lastId = payload.getId();
            String sortField = payload.getSortField();
            String sortValue = payload.getSortValue();

            if (sortField == null || sortField.equals("id")) {
                return cb.greaterThan(root.get("id"), lastId);
            }

            // WHERE (nombre > 'valor') OR (nombre = 'valor' AND id > lastId)
            return cb.or(
                    cb.greaterThan(root.get(sortField), sortValue),
                    cb.and(
                            cb.equal(root.get(sortField), sortValue),
                            cb.greaterThan(root.get("id"), lastId)
                    )
            );
        };
    }
}
