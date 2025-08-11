package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

     Optional<Tecnico> findByNombres (String nombres);
     Optional<Tecnico> findById (Long idTecnico);

}
