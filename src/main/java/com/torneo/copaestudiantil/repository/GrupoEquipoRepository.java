package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.GrupoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoEquipoRepository extends JpaRepository<GrupoEquipo, Long> {

    List<GrupoEquipo> findByGrupoId(Long grupoId);
    boolean existsByGrupoIdAndEquipoId(Long grupoId, Long equipoId);
}