package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.search.JugadorSearchRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.entity.Jugador;
import com.torneo.copaestudiantil.entity.TipoDocumento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class JugadorSpecification {

    public static Specification<Jugador> fromRequest(JugadorSearchRequest req) {
        CursorRequest pag = req.getPagination() != null
                ? req.getPagination() : new CursorRequest();
        String cursor = pag.getNextCursor();
        String direction = pag.getDirection();   // CursorRequest ya garantiza "ASC" por defecto

        return Specification
                .where(conActivo(req.getActivo()))
                .and(conBusquedaGeneral(req.getBusqueda()))
                .and(conNombres(req.getNombres()))
                .and(conApellidoPaterno(req.getApellidoPaterno()))
                .and(conTipoDocumento(req.getTipoDocumento()))
                .and(conNumeroDocumento(req.getNumeroDocumento()))
                .and(conNacionalidad(req.getNacionalidad()))
                .and(conPosicion(req.getPosicion()))
                .and(conAnioNacimientoDesde(req.getAnioNacimientoDesde()))
                .and(conAnioNacimientoHasta(req.getAnioNacimientoHasta()))
                .and(conFoto(req.getTieneFoto()))
                .and(conEquipoInscrito(req.getEquipoId()))
                .and(conCategoriaInscrita(req.getCategoriaId()))
                .and(conEdicionInscrito(req.getEdicionId()))
                .and(desdeCursor(cursor, direction));
    }

    public static Specification<Jugador> conActivo(Boolean activo) {
        return (root, query, cb) ->
                activo == null ? null : cb.equal(root.get("activo"), activo);
    }

    /**
     * Busca el término al INICIO de nombres O apellido paterno O apellido materno.
     * Usa prefijo ('texto%') SIN lower() para aprovechar los índices.
     * La insensibilidad a mayúsculas la da la BD (MySQL ci por defecto; H2 configurado igual).
     * Ej: "rey" encuentra "Reyes"; "eder" encuentra "Eder".
     */
    public static Specification<Jugador> conBusquedaGeneral(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String prefijo = q.trim() + "%";
            return cb.or(
                    cb.like(root.get("nombres"), prefijo),
                    cb.like(root.get("apellidoPaterno"), prefijo),
                    cb.like(root.get("apellidoMaterno"), prefijo));
        };
    }

    /** PREFIJO — usa índice. Ej: "Car" → "Carlos", "Carmen". */
    public static Specification<Jugador> conNombres(String nombres) {
        return (root, query, cb) ->
                nombres == null || nombres.isBlank() ? null
                        : cb.like(root.get("nombres"), nombres.trim() + "%");
    }

    /** PREFIJO — usa índice. Ej: "Ram" → "Ramirez", "Ramos". */
    public static Specification<Jugador> conApellidoPaterno(String apellido) {
        return (root, query, cb) ->
                apellido == null || apellido.isBlank() ? null
                        : cb.like(root.get("apellidoPaterno"), apellido.trim() + "%");
    }

    public static Specification<Jugador> conTipoDocumento(TipoDocumento tipo) {
        return (root, query, cb) ->
                tipo == null ? null : cb.equal(root.get("tipoDocumento"), tipo);
    }

    /** EXACTO — filtra por posición en cancha. */
    public static Specification<Jugador> conPosicion(
            com.torneo.copaestudiantil.entity.PosicionJugador posicion) {
        return (root, query, cb) ->
                posicion == null ? null : cb.equal(root.get("posicion"), posicion);
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
     * Jugadores inscritos (activos) en un equipo.
     * Subquery sobre inscripciones_jugadores: id del jugador IN (jugadores del equipo).
     */
    public static Specification<Jugador> conEquipoInscrito(Long equipoId) {
        return (root, query, cb) -> {
            if (equipoId == null) return null;
            var sub = query.subquery(Long.class);
            var insc = sub.from(com.torneo.copaestudiantil.entity.InscripcionJugador.class);
            sub.select(insc.get("jugador").get("id"))
               .where(cb.and(
                       cb.equal(insc.get("equipo").get("id"), equipoId),
                       cb.isTrue(insc.get("activo"))));
            return root.get("id").in(sub);
        };
    }

    /**
     * Jugadores inscritos (activos) en equipos de una categoría.
     * Navega inscripción → equipo → categoría.
     */
    public static Specification<Jugador> conCategoriaInscrita(Long categoriaId) {
        return (root, query, cb) -> {
            if (categoriaId == null) return null;
            var sub = query.subquery(Long.class);
            var insc = sub.from(com.torneo.copaestudiantil.entity.InscripcionJugador.class);
            sub.select(insc.get("jugador").get("id"))
               .where(cb.and(
                       cb.equal(insc.get("equipo").get("categoria").get("id"), categoriaId),
                       cb.isTrue(insc.get("activo"))));
            return root.get("id").in(sub);
        };
    }

    /**
     * Jugadores inscritos (activos) en una edición.
     */
    public static Specification<Jugador> conEdicionInscrito(Long edicionId) {
        return (root, query, cb) -> {
            if (edicionId == null) return null;
            var sub = query.subquery(Long.class);
            var insc = sub.from(com.torneo.copaestudiantil.entity.InscripcionJugador.class);
            sub.select(insc.get("jugador").get("id"))
               .where(cb.and(
                       cb.equal(insc.get("edicion").get("id"), edicionId),
                       cb.isTrue(insc.get("activo"))));
            return root.get("id").in(sub);
        };
    }

    /**
     * Cursor enriquecido con desempate por sortField + id, respetando la dirección.
     * ASC  → trae registros DESPUÉS del último (id > lastId)
     * DESC → trae registros DESPUÉS del último en orden inverso (id < lastId)
     * Esto es lo que hace que "Cargar más" funcione con orden más-reciente-arriba.
     */
    public static Specification<Jugador> desdeCursor(String cursor, String direction) {
        return (root, query, cb) -> {
            if (cursor == null || cursor.isBlank()) return null;

            CursorUtil.CursorPayload payload = CursorUtil.decode(cursor);
            if (payload == null) return null;

            Long lastId = payload.getId();
            String sortField = payload.getSortField();
            String sortValue = payload.getSortValue();
            boolean desc = "DESC".equalsIgnoreCase(direction);

            if (sortField == null || sortField.equals("id")) {
                return desc
                        ? cb.lessThan(root.get("id"), lastId)
                        : cb.greaterThan(root.get("id"), lastId);
            }

            if (desc) {
                return cb.or(
                        cb.lessThan(root.get(sortField), sortValue),
                        cb.and(
                                cb.equal(root.get(sortField), sortValue),
                                cb.lessThan(root.get("id"), lastId)
                        )
                );
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

    /** Compatibilidad: versión sin dirección (asume ASC). */
    public static Specification<Jugador> desdeCursor(String cursor) {
        return desdeCursor(cursor, "ASC");
    }
}
