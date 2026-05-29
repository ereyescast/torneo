package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.JugadorSearchRequest;
import com.torneo.copaestudiantil.entity.Jugador;
import com.torneo.copaestudiantil.entity.TipoDocumento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class JugadorSpecification {

    public static Specification<Jugador> fromRequest(JugadorSearchRequest req) {
        String cursor = req.getPagination() != null
                ? req.getPagination().getNextCursor() : null;

        return Specification
                .where(conActivo(req.getActivo()))
                .and(conNombres(req.getNombres()))
                .and(conApellidoPaterno(req.getApellidoPaterno()))
                .and(conTipoDocumento(req.getTipoDocumento()))
                .and(conNumeroDocumento(req.getNumeroDocumento()))
                .and(conNacionalidad(req.getNacionalidad()))
                .and(conAnioNacimientoDesde(req.getAnioNacimientoDesde()))
                .and(conAnioNacimientoHasta(req.getAnioNacimientoHasta()))
                .and(conFoto(req.getTieneFoto()))
                .and(desdeCursor(cursor));
    }

    public static Specification<Jugador> conActivo(Boolean activo) {
        return (root, query, cb) ->
                activo == null ? null : cb.equal(root.get("activo"), activo);
    }

    /** PREFIJO — usa índice. Ej: "Car" → "Carlos", "Carmen". */
    public static Specification<Jugador> conNombres(String nombres) {
        return (root, query, cb) ->
                nombres == null || nombres.isBlank() ? null
                        : cb.like(cb.lower(root.get("nombres")),
                        nombres.toLowerCase().trim() + "%");
    }

    /** PREFIJO — usa índice. Ej: "Ram" → "Ramirez", "Ramos". */
    public static Specification<Jugador> conApellidoPaterno(String apellido) {
        return (root, query, cb) ->
                apellido == null || apellido.isBlank() ? null
                        : cb.like(cb.lower(root.get("apellidoPaterno")),
                        apellido.toLowerCase().trim() + "%");
    }

    public static Specification<Jugador> conTipoDocumento(TipoDocumento tipo) {
        return (root, query, cb) ->
                tipo == null ? null : cb.equal(root.get("tipoDocumento"), tipo);
    }

    /** EXACTO — el documento es único, el usuario lo sabe completo. */
    public static Specification<Jugador> conNumeroDocumento(String documento) {
        return (root, query, cb) ->
                documento == null || documento.isBlank() ? null
                        : cb.equal(root.get("numeroDocumento"), documento.trim());
    }

    /** EXACTO — valor fijo. El frontend usa select con valores disponibles. */
    public static Specification<Jugador> conNacionalidad(String nacionalidad) {
        return (root, query, cb) ->
                nacionalidad == null || nacionalidad.isBlank() ? null
                        : cb.equal(cb.lower(root.get("nacionalidad")),
                        nacionalidad.toLowerCase().trim());
    }

    public static Specification<Jugador> conAnioNacimientoDesde(Integer anio) {
        return (root, query, cb) ->
                anio == null ? null
                        : cb.greaterThanOrEqualTo(root.get("fechaNacimiento"),
                        LocalDate.of(anio, 1, 1));
    }

    public static Specification<Jugador> conAnioNacimientoHasta(Integer anio) {
        return (root, query, cb) ->
                anio == null ? null
                        : cb.lessThanOrEqualTo(root.get("fechaNacimiento"),
                        LocalDate.of(anio, 12, 31));
    }

    public static Specification<Jugador> conFoto(Boolean tieneFoto) {
        return (root, query, cb) -> {
            if (tieneFoto == null) return null;
            return tieneFoto
                    ? cb.isNotNull(root.get("profileImage"))
                    : cb.isNull(root.get("profileImage"));
        };
    }

    /**
     * Cursor enriquecido con desempate por sortField + id.
     * Si sortField = "id" → WHERE id > lastId
     * Si sortField = "apellidoPaterno" →
     *   WHERE (apellidoPaterno > 'Ramirez')
     *      OR (apellidoPaterno = 'Ramirez' AND id > 5)
     */
    public static Specification<Jugador> desdeCursor(String cursor) {
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
