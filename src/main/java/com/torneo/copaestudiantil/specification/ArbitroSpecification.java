package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.ArbitroSearchRequest;
import com.torneo.copaestudiantil.entity.Arbitro;
import org.springframework.data.jpa.domain.Specification;

public class ArbitroSpecification {

    public static Specification<Arbitro> fromRequest(ArbitroSearchRequest req) {
        String cursor = req.getPagination() != null
                ? req.getPagination().getNextCursor() : null;

        return Specification
                .where(conActivo(req.getActivo()))
                .and(conNombre(req.getNombre()))
                .and(conEmail(req.getEmail()))
                .and(conOrganizador(req.getOrganizadorId()))
                .and(desdeCursor(cursor));
    }

    public static Specification<Arbitro> conActivo(Boolean activo) {
        return (root, query, cb) ->
                activo == null ? null : cb.equal(root.get("activo"), activo);
    }

    /** PREFIJO — usa índice. */
    public static Specification<Arbitro> conNombre(String nombre) {
        return (root, query, cb) ->
                nombre == null || nombre.isBlank() ? null
                        : cb.like(cb.lower(root.get("nombre")),
                        nombre.toLowerCase().trim() + "%");
    }

    /** EXACTO — el email es único. */
    public static Specification<Arbitro> conEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank() ? null
                        : cb.equal(cb.lower(root.get("email")),
                        email.toLowerCase().trim());
    }

    public static Specification<Arbitro> conOrganizador(Long organizadorId) {
        return (root, query, cb) ->
                organizadorId == null ? null
                        : cb.equal(root.get("organizadorId"), organizadorId);
    }

    public static Specification<Arbitro> desdeCursor(String cursor) {
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
