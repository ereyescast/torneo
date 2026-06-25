package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.GrupoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoEquipoRepository extends JpaRepository<GrupoEquipo, Long> {

    List<GrupoEquipo> findByGrupoId(Long grupoId);
    boolean existsByGrupoIdAndEquipoId(Long grupoId, Long equipoId);

    /** Asignación activa de un equipo a un grupo (para saber en qué grupo está). */
    java.util.Optional<GrupoEquipo> findFirstByEquipoIdAndActivoTrue(Long equipoId);

    /** Cuántos equipos activos tiene un grupo (para validar el límite). */
    long countByGrupoIdAndActivoTrue(Long grupoId);

    /** Fila (activa o no) de un equipo en un grupo, para reactivarla al reasignar. */
    java.util.Optional<GrupoEquipo> findFirstByGrupoIdAndEquipoId(Long grupoId, Long equipoId);
}