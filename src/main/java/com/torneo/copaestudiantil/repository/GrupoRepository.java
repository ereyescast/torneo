package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByEdicionIdAndCategoriaId(Long edicionId, Long categoriaId);
    List<Grupo> findByEdicionIdAndCategoriaIdAndActivoTrue(Long edicionId, Long categoriaId);
}