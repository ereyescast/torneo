package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {

    List<Arbitro> findByOrganizadorIdAndActivoTrue(Long organizadorId);

    Optional<Arbitro> findByIdAndOrganizadorId(Long id, Long organizadorId);
}