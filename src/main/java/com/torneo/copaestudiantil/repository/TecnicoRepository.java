package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TecnicoRepository
        extends JpaRepository<Tecnico, Long>,
        JpaSpecificationExecutor<Tecnico> {

     boolean existsByNumeroDocumento(String numeroDocumento);
}