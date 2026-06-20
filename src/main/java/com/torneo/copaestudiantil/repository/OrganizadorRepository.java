package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrganizadorRepository extends JpaRepository<Organizador, Long>,
        JpaSpecificationExecutor<Organizador> {

    Optional<Organizador> findByNombre(String nombre);
    boolean existsByNombre(String nombre);

    /** Para resolver el organizador desde la URL pública (slug). */
    Optional<Organizador> findByCodigoPublico(String codigoPublico);
    boolean existsByCodigoPublico(String codigoPublico);
}
