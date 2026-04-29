package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

    List<Sede> findByOrganizadorId(Long organizadorId);
}