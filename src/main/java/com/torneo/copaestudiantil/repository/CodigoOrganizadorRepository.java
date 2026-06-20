package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.CodigoOrganizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodigoOrganizadorRepository extends JpaRepository<CodigoOrganizador, Long> {

    Optional<CodigoOrganizador> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<CodigoOrganizador> findAllByOrderByFechaCreacionDesc();
}
