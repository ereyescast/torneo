package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.SuspensionJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspensionJugadorRepository extends JpaRepository<SuspensionJugador, Long> {

    /** Verifica si un jugador está suspendido en una fecha específica */
    boolean existsByJugadorIdAndEdicionIdAndFechaSuspensionAndActivoTrue(
            Long jugadorId, Long edicionId, Integer fechaSuspension);

    /** Lista suspensiones activas de un jugador en una edición */
    List<SuspensionJugador> findByJugadorIdAndEdicionIdAndActivoTrue(
            Long jugadorId, Long edicionId);

    /** Lista todas las suspensiones de una edición */
    List<SuspensionJugador> findByEdicionIdAndActivoTrue(Long edicionId);

    /** Lista suspensiones por partido origen */
    List<SuspensionJugador> findByPartidoOrigenId(Long partidoId);
}
