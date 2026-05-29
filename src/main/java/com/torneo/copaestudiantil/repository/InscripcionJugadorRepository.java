package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.InscripcionJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionJugadorRepository extends JpaRepository<InscripcionJugador, Long>,
        JpaSpecificationExecutor<InscripcionJugador> {

    List<InscripcionJugador> findByEquipoId(Long equipoId);
    List<InscripcionJugador> findByEdicionId(Long edicionId);
    Optional<InscripcionJugador> findByJugadorIdAndEdicionId(Long jugadorId, Long edicionId);
    boolean existsByJugadorIdAndEdicionId(Long jugadorId, Long edicionId);
    List<InscripcionJugador> findByJugadorId(Long jugadorId);
    List<InscripcionJugador> findByEquipoIdAndActivo(Long equipoId, Boolean activo);
}
