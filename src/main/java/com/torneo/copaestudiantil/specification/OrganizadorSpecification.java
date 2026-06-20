package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.common.util.SlugUtils;
import com.torneo.copaestudiantil.entity.Organizador;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification para el DIRECTORIO PÚBLICO de torneos.
 *
 * A diferencia de las demás specifications (admin), esta NO filtra por
 * organizadorId — es pública. Solo muestra torneos activos y visibles,
 * con búsqueda opcional por nombre o ciudad, INSENSIBLE A TILDES.
 */
public class OrganizadorSpecification {

    /** Combina los filtros del directorio público. */
    public static Specification<Organizador> directorioPublico(String q) {
        return Specification
                .where(soloActivos())
                .and(soloVisibles())
                .and(conNombreOCiudad(q));
    }

    /** Solo torneos activos. */
    public static Specification<Organizador> soloActivos() {
        return (root, query, cb) -> cb.equal(root.get("activo"), true);
    }

    /** Solo torneos marcados como visibles en el directorio. */
    public static Specification<Organizador> soloVisibles() {
        return (root, query, cb) -> cb.equal(root.get("visibleEnDirectorio"), true);
    }

    /**
     * Búsqueda LIKE insensible a tildes.
     * El texto buscado se normaliza (quita tildes, minúsculas) y se compara
     * contra:
     *   - nombreBusqueda → el nombre ya normalizado en la BD (sin tildes)
     *   - direccion      → pasada a minúsculas (cubre la mayoría de casos)
     * Si q es null o vacío, no aplica filtro (devuelve todos).
     */
    public static Specification<Organizador> conNombreOCiudad(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String patron = "%" + SlugUtils.normalizarBusqueda(q) + "%";
            return cb.or(
                    cb.like(root.get("nombreBusqueda"), patron),
                    cb.like(cb.lower(root.get("direccion")), patron)
            );
        };
    }
}
