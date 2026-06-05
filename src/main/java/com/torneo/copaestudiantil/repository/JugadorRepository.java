package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long>,
        JpaSpecificationExecutor<Jugador> {

    // Documento único POR ORGANIZADOR
    Optional<Jugador> findByNumeroDocumentoAndOrganizadorId(String numeroDocumento, Long organizadorId);
    boolean existsByNumeroDocumentoAndOrganizadorId(String numeroDocumento, Long organizadorId);

    List<Jugador> findByActivo(Boolean activo);
}
