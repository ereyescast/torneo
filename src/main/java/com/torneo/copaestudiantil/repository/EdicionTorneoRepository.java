package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EdicionTorneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EdicionTorneoRepository extends JpaRepository<EdicionTorneo, Long> {

    // Buscar todas las ediciones de un organizador
    List<EdicionTorneo> findByOrganizadorId(Long organizadorId);

    // Buscar edición por organizador y nombre (ej: "Enero 2026")
    Optional<EdicionTorneo> findByOrganizadorIdAndNombre(Long organizadorId, String nombre);

    // Buscar ediciones activas
    List<EdicionTorneo> findByActivaTrue();
}