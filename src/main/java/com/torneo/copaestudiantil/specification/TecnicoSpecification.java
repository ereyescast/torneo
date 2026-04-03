package com.torneo.copaestudiantil.specification;

import com.torneo.copaestudiantil.entity.Tecnico;
import org.springframework.data.jpa.domain.Specification;

public class TecnicoSpecification {

    public static Specification<Tecnico> activo() {
        return (root, query, cb) ->
                cb.isTrue(root.get("activo"));
    }

    public static Specification<Tecnico> nombresLike(String nombres) {
        return (root, query, cb) ->
                nombres == null || nombres.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("nombres")),
                        "%" + nombres.toLowerCase() + "%"
                );
    }

    public static Specification<Tecnico> documentoEquals(String numeroDocumento) {
        return (root, query, cb) ->
                numeroDocumento == null || numeroDocumento.isBlank()
                        ? null
                        : cb.equal(
                        root.get("numeroDocumento"),
                        numeroDocumento
                );
    }

    public static Specification<Tecnico> nacionalidadLike(String nacionalidad) {
        return (root, query, cb) ->
                nacionalidad == null || nacionalidad.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("nacionalidad")),
                        "%" + nacionalidad.toLowerCase() + "%"
                );
    }
}