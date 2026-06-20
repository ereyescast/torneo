package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadisticaJugador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaJugadorRepository extends JpaRepository<EstadisticaJugador, Long> {

    List<EstadisticaJugador> findByPartidoId(Long partidoId);
    List<EstadisticaJugador> findByEquipoIdAndEdicionId(Long equipoId, Long edicionId);
    List<EstadisticaJugador> findByJugadorIdAndEdicionId(Long jugadorId, Long edicionId);
    Optional<EstadisticaJugador> findByJugadorIdAndPartidoId(Long jugadorId, Long partidoId);

    /**
     * Ranking de goleadores de una edición + categoría (con asistencias).
     * Si fase es null, incluye todas las fases.
     *
     * El parámetro Pageable se usa SOLO para limitar el resultado a un Top N
     * (ej: PageRequest.of(0, 30) = los 30 máximos goleadores). No es paginación
     * completa: un ranking se consume como "los mejores N", no recorriéndolo entero.
     */
    @Query("""
            SELECT e.jugador.id              AS jugadorId,
                   e.jugador.nombres         AS nombres,
                   e.jugador.apellidoPaterno AS apellidoPaterno,
                   e.jugador.apellidoMaterno AS apellidoMaterno,
                   e.equipo.nombre           AS equipoNombre,
                   SUM(e.goles)              AS totalGoles,
                   SUM(e.asistencias)        AS totalAsistencias
            FROM EstadisticaJugador e
            WHERE e.edicion.id = :edicionId
              AND e.partido.categoria.id = :categoriaId
              AND e.organizadorId = :organizadorId
              AND (:fase IS NULL OR e.partido.fase = :fase)
            GROUP BY e.jugador.id,
                     e.jugador.nombres,
                     e.jugador.apellidoPaterno,
                     e.jugador.apellidoMaterno,
                     e.equipo.nombre
            HAVING SUM(e.goles) > 0
            ORDER BY SUM(e.goles) DESC,
                     SUM(e.asistencias) DESC
            """)
    List<Object[]> rankingGoleadores(
            @Param("organizadorId") Long organizadorId,
            @Param("edicionId") Long edicionId,
            @Param("categoriaId") Long categoriaId,
            @Param("fase") String fase,
            Pageable topN);

    /**
     * Ranking de tarjetas de una edición + categoría.
     * Ordena por rojas primero (más grave), luego amarillas.
     * El Pageable se usa solo para limitar al Top N.
     */
    @Query("""
            SELECT e.jugador.id              AS jugadorId,
                   e.jugador.nombres         AS nombres,
                   e.jugador.apellidoPaterno AS apellidoPaterno,
                   e.jugador.apellidoMaterno AS apellidoMaterno,
                   e.equipo.nombre           AS equipoNombre,
                   SUM(e.tarjetasAmarillas)  AS totalAmarillas,
                   SUM(e.tarjetasRojas)      AS totalRojas
            FROM EstadisticaJugador e
            WHERE e.edicion.id = :edicionId
              AND e.partido.categoria.id = :categoriaId
              AND e.organizadorId = :organizadorId
              AND (:fase IS NULL OR e.partido.fase = :fase)
            GROUP BY e.jugador.id,
                     e.jugador.nombres,
                     e.jugador.apellidoPaterno,
                     e.jugador.apellidoMaterno,
                     e.equipo.nombre
            HAVING SUM(e.tarjetasAmarillas) > 0 OR SUM(e.tarjetasRojas) > 0
            ORDER BY SUM(e.tarjetasRojas) DESC,
                     SUM(e.tarjetasAmarillas) DESC
            """)
    List<Object[]> rankingTarjetas(
            @Param("organizadorId") Long organizadorId,
            @Param("edicionId") Long edicionId,
            @Param("categoriaId") Long categoriaId,
            @Param("fase") String fase,
            Pageable topN);

    /**
     * Estadísticas acumuladas de TODOS los jugadores de un equipo
     * (sumando todos los partidos de ese equipo en la edición).
     *
     * Devuelve una fila por jugador que registró al menos una estadística:
     *   [0] jugadorId
     *   [1] totalGoles
     *   [2] totalAsistencias
     *   [3] totalAmarillas
     *   [4] totalRojas
     *   [5] partidosConRegistro  (cuántos partidos distintos tienen estadística suya)
     *
     * Se usa en el plantel público para mostrar las cifras junto a cada jugador.
     * Los jugadores sin ninguna estadística NO aparecen aquí; el service los
     * completa con ceros a partir del padrón de inscripciones.
     */
    @Query("""
            SELECT e.jugador.id            AS jugadorId,
                   SUM(e.goles)            AS totalGoles,
                   SUM(e.asistencias)      AS totalAsistencias,
                   SUM(e.tarjetasAmarillas) AS totalAmarillas,
                   SUM(e.tarjetasRojas)    AS totalRojas,
                   COUNT(DISTINCT e.partido.id) AS partidos
            FROM EstadisticaJugador e
            WHERE e.equipo.id = :equipoId
            GROUP BY e.jugador.id
            """)
    List<Object[]> statsPorEquipo(@Param("equipoId") Long equipoId);
}
