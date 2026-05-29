package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long>,
        JpaSpecificationExecutor<Equipo> {

    List<Equipo> findByEdicionIdAndCategoriaId(Long edicionId, Long categoriaId);
    List<Equipo> findByEdicionIdAndCategoriaIdAndActivo(Long edicionId, Long categoriaId, Boolean activo);
    List<Equipo> findByOrganizadorId(Long organizadorId);
    List<Equipo> findByActivo(Boolean activo);
    List<Equipo> findByOrganizadorIdAndActivo(Long organizadorId, Boolean activo);
}
