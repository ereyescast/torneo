package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.TecnicoSearchRequest;
import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.entity.TipoDocumento;
import org.springframework.data.jpa.domain.Specification;

public class TecnicoSpecification {



    public static Specification<Tecnico> fromRequest(TecnicoSearchRequest req) {
        String cursor = req.getPagination() != null
                ? req.getPagination().getNextCursor() : null;

        return Specification
                .where(conActivo(req.getActivo()))
                .and(conNombres(req.getNombres()))
                .and(conApellidosPaterno(req.getApellidosPaterno()))
                .and(conTipoDocumento(req.getTipoDocumento()))
                .and(conNumeroDocumento(req.getNumeroDocumento()))
                .and(conNacionalidad(req.getNacionalidad()))
                .and(desdeCursor(cursor));
    }



    public static Specification<Tecnico> conActivo(Boolean activo) {
        return (root, query, cb) ->
                activo == null ? null : cb.equal(root.get("activo"), activo);
    }

    /** PREFIJO — usa índice. */
    public static Specification<Tecnico> conNombres(String nombres) {
        return (root, query, cb) ->
                nombres == null || nombres.isBlank() ? null
                        : cb.like(cb.lower(root.get("nombres")),
                        "%" + nombres.toLowerCase().trim() + "%");
    }

    /** PREFIJO — usa índice. */
    public static Specification<Tecnico> conApellidosPaterno(String apellido) {
        return (root, query, cb) ->
                apellido == null || apellido.isBlank() ? null
                        : cb.like(cb.lower(root.get("apellidosPaterno")),
                        "%" + apellido.toLowerCase().trim() + "%");
    }

    public static Specification<Tecnico> conTipoDocumento(TipoDocumento tipo) {
        return (root, query, cb) ->
                tipo == null ? null : cb.equal(root.get("tipoDocumento"), tipo);
    }

    /** EXACTO — el documento es único. */
    public static Specification<Tecnico> conNumeroDocumento(String documento) {
        return (root, query, cb) ->
                documento == null || documento.isBlank() ? null
                        : cb.equal(root.get("numeroDocumento"), documento.trim());
    }

    /** EXACTO — valor fijo, el frontend usa select. */
    public static Specification<Tecnico> conNacionalidad(String nacionalidad) {
        return (root, query, cb) ->
                nacionalidad == null || nacionalidad.isBlank() ? null
                        : cb.equal(cb.lower(root.get("nacionalidad")),
                        nacionalidad.toLowerCase().trim());
    }

    /**
     * Cursor enriquecido con desempate por sortField + id.
     */
    public static Specification<Tecnico> desdeCursor(String cursor) {
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

    // ── Métodos legacy ───────────────────────────────────────────────────────
    public static Specification<Tecnico> activo()                   { return conActivo(true); }
    public static Specification<Tecnico> nombresLike(String n)      { return conNombres(n); }
    public static Specification<Tecnico> documentoEquals(String d)  { return conNumeroDocumento(d); }
    public static Specification<Tecnico> nacionalidadLike(String n) { return conNacionalidad(n); }


}
