package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.CategoriaSearchRequest;
import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.ModalidadJuego;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import org.springframework.data.jpa.domain.Specification;

public class CategoriaSpecification {

    public static Specification<Categoria> fromRequest(CategoriaSearchRequest req) {
        String cursor = req.getPagination() != null
                ? req.getPagination().getNextCursor() : null;

        return Specification
                .where(conActiva(req.getActiva()))
                .and(conOrganizador(req.getOrganizadorId()))
                .and(conEdicion(req.getEdicionId()))
                .and(conAnioNacimiento(req.getAnioNacimiento()))
                .and(conNivel(req.getNivel()))
                .and(conModalidad(req.getModalidad()))
                .and(desdeCursor(cursor));
    }

    public static Specification<Categoria> conActiva(Boolean activa) {
        return (root, query, cb) ->
                activa == null ? null : cb.equal(root.get("activa"), activa);
    }

    public static Specification<Categoria> conOrganizador(Long organizadorId) {
        return (root, query, cb) ->
                organizadorId == null ? null
                        : cb.equal(root.get("organizadorId"), organizadorId);
    }

    public static Specification<Categoria> conEdicion(Long edicionId) {
        return (root, query, cb) ->
                edicionId == null ? null
                        : cb.equal(root.get("edicion").get("id"), edicionId);
    }

    /** EXACTO — el año es un valor numérico preciso. */
    public static Specification<Categoria> conAnioNacimiento(Integer anio) {
        return (root, query, cb) ->
                anio == null ? null : cb.equal(root.get("anioNacimiento"), anio);
    }

    /** EXACTO — enum. */
    public static Specification<Categoria> conNivel(NivelCompetencia nivel) {
        return (root, query, cb) ->
                nivel == null ? null : cb.equal(root.get("nivel"), nivel);
    }

    /** EXACTO — enum. */
    public static Specification<Categoria> conModalidad(ModalidadJuego modalidad) {
        return (root, query, cb) ->
                modalidad == null ? null : cb.equal(root.get("modalidad"), modalidad);
    }

    public static Specification<Categoria> desdeCursor(String cursor) {
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
