package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import com.torneo.copaestudiantil.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    List<Partido> findByEdicionIdAndCategoriaId(
            Long edicionId, Long categoriaId);

    List<Partido> findByEdicionIdAndCategoriaIdAndEstado(
            Long edicionId, Long categoriaId, EstadoPartido estado);

    List<Partido> findByEquipoLocalIdOrEquipoVisitanteId(
            Long equipoLocalId, Long equipoVisitanteId);

    /** Todos los partidos de una fase concreta (ej: GRUPOS, FINAL_ORO) */
    List<Partido> findByEdicionIdAndCategoriaIdAndFase(
            Long edicionId, Long categoriaId, FasePartido fase);

    /** Partidos de un grupo específico (siempre fase=GRUPOS) */
    List<Partido> findByGrupoId(Long grupoId);

    /** Partidos de un grupo filtrados también por estado */
    List<Partido> findByGrupoIdAndEstado(Long grupoId, EstadoPartido estado);
}