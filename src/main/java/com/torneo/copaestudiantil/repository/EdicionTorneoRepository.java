package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EdicionTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EdicionTorneoRepository extends JpaRepository<EdicionTorneo, Long>,
        JpaSpecificationExecutor<EdicionTorneo> {

    List<EdicionTorneo> findByOrganizadorId(Long organizadorId);
    Optional<EdicionTorneo> findByOrganizadorIdAndNombre(Long organizadorId, String nombre);
    List<EdicionTorneo> findByActivaTrue();
    List<EdicionTorneo> findByActiva(Boolean activa);
    List<EdicionTorneo> findByOrganizadorIdAndActiva(Long organizadorId, Boolean activa);

    /**
     * Verifica existencia exacta — usado por crear() como red de seguridad.
     */
    boolean existsByOrganizadorIdAndNombreAndFechaInicio(
            Long organizadorId, String nombre, LocalDate fechaInicio);

    /**
     * Devuelve la edición existente (si la hay) para que el endpoint /existe
     * pueda mostrar al organizador cuál es y cuándo la creó.
     */
    Optional<EdicionTorneo> findByOrganizadorIdAndNombreAndFechaInicio(
            Long organizadorId, String nombre, LocalDate fechaInicio);
}
