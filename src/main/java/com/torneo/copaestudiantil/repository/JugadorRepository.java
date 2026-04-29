package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    Optional<Jugador> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);
}