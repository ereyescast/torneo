package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByEdicionIdAndCategoriaId(Long edicionId, Long categoriaId);

    List<Equipo> findByOrganizadorId(Long organizadorId);
}