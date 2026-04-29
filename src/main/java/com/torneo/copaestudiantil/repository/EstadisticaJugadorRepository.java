package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadisticaJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaJugadorRepository extends JpaRepository<EstadisticaJugador, Long> {

    List<EstadisticaJugador> findByPartidoId(Long partidoId);
    List<EstadisticaJugador> findByEquipoIdAndEdicionId(Long equipoId, Long edicionId);
    List<EstadisticaJugador> findByJugadorIdAndEdicionId(Long jugadorId, Long edicionId);
    Optional<EstadisticaJugador> findByJugadorIdAndPartidoId(Long jugadorId, Long partidoId);
}