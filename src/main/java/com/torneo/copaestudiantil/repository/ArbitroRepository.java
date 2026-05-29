package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ArbitroRepository extends JpaRepository<Arbitro, Long>,
        JpaSpecificationExecutor<Arbitro> {

    List<Arbitro> findByOrganizadorIdAndActivoTrue(Long organizadorId);

    Optional<Arbitro> findByIdAndOrganizadorId(Long id, Long organizadorId);
    List<Arbitro> findByActivo(Boolean activo);
    List<Arbitro> findByOrganizadorId(Long organizadorId);
    List<Arbitro> findByOrganizadorIdAndActivo(Long organizadorId, Boolean activo);
}
