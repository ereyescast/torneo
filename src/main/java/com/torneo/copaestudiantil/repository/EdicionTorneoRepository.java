package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EdicionTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EdicionTorneoRepository extends JpaRepository<EdicionTorneo, Long>,
        JpaSpecificationExecutor<EdicionTorneo> {

    List<EdicionTorneo> findByOrganizadorId(Long organizadorId);
    Optional<EdicionTorneo> findByOrganizadorIdAndNombre(Long organizadorId, String nombre);
    List<EdicionTorneo> findByActivaTrue();
    List<EdicionTorneo> findByActiva(Boolean activa);
    List<EdicionTorneo> findByOrganizadorIdAndActiva(Long organizadorId, Boolean activa);
}
