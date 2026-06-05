package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EdicionTorneoSpecification {

    public static Specification<EdicionTorneo> fromRequest(EdicionSearchRequest req) {
        String cursor = req.getPagination() != null
                ? req.getPagination().getNextCursor() : null;

        return Specification
                .where(conActiva(req.getActiva()))
                .and(conNombre(req.getNombre()))
                .and(conFechaInicioDesdE(req.getFechaInicioDesdE()))
                .and(conFechaInicioHasta(req.getFechaInicioHasta()))
                .and(desdeCursor(cursor));
    }

    public static Specification<EdicionTorneo> conActiva(Boolean activa) {
        return (root, query, cb) ->
                activa == null ? null : cb.equal(root.get("activa"), activa);
    }

    /** PREFIJO — usa índice. */
    public static Specification<EdicionTorneo> conNombre(String nombre) {
        return (root, query, cb) ->
                nombre == null || nombre.isBlank() ? null
                        : cb.like(cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase().trim() + "%");
    }

    public static Specification<EdicionTorneo> conFechaInicioDesdE(LocalDate desde) {
        return (root, query, cb) ->
                desde == null ? null
                        : cb.greaterThanOrEqualTo(root.get("fechaInicio"), desde);
    }

    public static Specification<EdicionTorneo> conFechaInicioHasta(LocalDate hasta) {
        return (root, query, cb) ->
                hasta == null ? null
                        : cb.lessThanOrEqualTo(root.get("fechaInicio"), hasta);
    }

    public static Specification<EdicionTorneo> desdeCursor(String cursor) {
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
