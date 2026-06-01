package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import com.torneo.copaestudiantil.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long>,
        JpaSpecificationExecutor<Partido> {

    List<Partido> findByEdicionIdAndCategoriaId(Long edicionId, Long categoriaId);

    List<Partido> findByEdicionIdAndCategoriaIdAndEstado(
            Long edicionId, Long categoriaId, EstadoPartido estado);

    List<Partido> findByEquipoLocalIdOrEquipoVisitanteId(
            Long equipoLocalId, Long equipoVisitanteId);

    List<Partido> findByEdicionIdAndCategoriaIdAndFase(
            Long edicionId, Long categoriaId, FasePartido fase);

    List<Partido> findByGrupoId(Long grupoId);

    List<Partido> findByGrupoIdAndEstado(Long grupoId, EstadoPartido estado);

    List<Partido> findByFixtureId(Long fixtureId);

    /**
     * Cuenta los WOs de un equipo en una edición (local o visitante).
     * Art. 16b — 2 WOs = eliminación automática del torneo.
     */
    @Query("""
        SELECT COUNT(p) FROM Partido p
        WHERE p.estado = 'WO'
          AND p.edicion.id = :edicionId
          AND (p.equipoLocal.id = :equipoId OR p.equipoVisitante.id = :equipoId)
          AND (
            (p.equipoLocal.id = :equipoId AND p.golesLocal = 0)
            OR
            (p.equipoVisitante.id = :equipoId AND p.golesVisitante = 0)
          )
        """)
    long contarWosPorEquipoEnEdicion(
            @Param("equipoId") Long equipoId,
            @Param("edicionId") Long edicionId);

    /**
     * Historial completo de partidos de un equipo en una edición.
     * Art. 14 — endpoint historial por equipo.
     */
    @Query("""
        SELECT p FROM Partido p
        WHERE p.edicion.id = :edicionId
          AND (p.equipoLocal.id = :equipoId OR p.equipoVisitante.id = :equipoId)
        ORDER BY p.fechaHora ASC
        """)
    List<Partido> findHistorialPorEquipoYEdicion(
            @Param("equipoId") Long equipoId,
            @Param("edicionId") Long edicionId);

    /**
     * Historial completo de un equipo en todas las ediciones.
     */
    @Query("""
        SELECT p FROM Partido p
        WHERE (p.equipoLocal.id = :equipoId OR p.equipoVisitante.id = :equipoId)
        ORDER BY p.fechaHora DESC
        """)
    List<Partido> findHistorialCompleto(@Param("equipoId") Long equipoId);
}
