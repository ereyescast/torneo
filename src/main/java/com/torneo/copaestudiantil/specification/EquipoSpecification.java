package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.EquipoSearchRequest;
import com.torneo.copaestudiantil.entity.Equipo;
import org.springframework.data.jpa.domain.Specification;

public class EquipoSpecification {

    public static Specification<Equipo> fromRequest(EquipoSearchRequest req) {
        return Specification
                .where(conActivo(req.getActivo()))
                .and(conNombre(req.getNombre()))
                .and(conOrganizador(req.getOrganizadorId()))
                .and(conEdicion(req.getEdicionId()))
                .and(conCategoria(req.getCategoriaId()))
                .and(conSede(req.getSedeId()))
                .and(desdeCursor(req.getPagination() != null
                        ? req.getPagination().getNextCursor() : null));
    }

    public static Specification<Equipo> conActivo(Boolean activo) {
        return (root, query, cb) ->
                activo == null ? null : cb.equal(root.get("activo"), activo);
    }

    public static Specification<Equipo> conNombre(String nombre) {
        return (root, query, cb) ->
                nombre == null || nombre.isBlank() ? null
                        : cb.like(cb.lower(root.get("nombre")),
                        nombre.toLowerCase().trim() + "%");
    }

    public static Specification<Equipo> conOrganizador(Long organizadorId) {
        return (root, query, cb) ->
                organizadorId == null ? null
                        : cb.equal(root.get("organizadorId"), organizadorId);
    }

    public static Specification<Equipo> conEdicion(Long edicionId) {
        return (root, query, cb) ->
                edicionId == null ? null
                        : cb.equal(root.get("edicion").get("id"), edicionId);
    }

    public static Specification<Equipo> conCategoria(Long categoriaId) {
        return (root, query, cb) ->
                categoriaId == null ? null
                        : cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Equipo> conSede(Long sedeId) {
        return (root, query, cb) ->
                sedeId == null ? null
                        : cb.equal(root.get("sede").get("id"), sedeId);
    }

    public static Specification<Equipo> desdeCursor(String cursor) {
        return (root, query, cb) -> {
            if (cursor == null || cursor.isBlank()) return null;

            CursorUtil.CursorPayload payload = CursorUtil.decode(cursor);
            if (payload == null) return null;

            Long lastId = payload.getId();
            String sortField = payload.getSortField();
            String sortValue = payload.getSortValue();

            // Si el cursor es solo por id (caso simple)
            if (sortField == null || sortField.equals("id")) {
                return cb.greaterThan(root.get("id"), lastId);
            }

            // Cursor enriquecido: desempate por sortField + id
            // WHERE (nombre > 'Ramirez') OR (nombre = 'Ramirez' AND id > 1)
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
