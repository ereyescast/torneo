package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadoPartido;
import com.torneo.copaestudiantil.entity.FasePartido;
import com.torneo.copaestudiantil.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long>,
        JpaSpecificationExecutor<Partido> {

    List<Partido> findByEdicionIdAndCategoriaId(Long edicionId, Long categoriaId);
    List<Partido> findByEdicionIdAndCategoriaIdAndEstado(Long edicionId, Long categoriaId, EstadoPartido estado);
    List<Partido> findByEquipoLocalIdOrEquipoVisitanteId(Long equipoLocalId, Long equipoVisitanteId);
    List<Partido> findByEdicionIdAndCategoriaIdAndFase(Long edicionId, Long categoriaId, FasePartido fase);
    List<Partido> findByGrupoId(Long grupoId);
    List<Partido> findByGrupoIdAndEstado(Long grupoId, EstadoPartido estado);
}
