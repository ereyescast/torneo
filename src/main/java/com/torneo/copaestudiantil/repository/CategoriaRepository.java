package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>,
        JpaSpecificationExecutor<Categoria> {

    // 🔎 Buscar categorías por edición
    List<Categoria> findByEdicion(EdicionTorneo edicion);

    // 🔎 Buscar categorías por edición ID (más práctico a veces)
    List<Categoria> findByEdicionId(Long edicionId);

    // 🔎 Buscar categorías activas
    List<Categoria> findByActivaTrue();
    List<Categoria> findByActiva(Boolean activa);
    // 🔎 Buscar categorías activas por edición
    List<Categoria> findByEdicionIdAndActivaTrue(Long edicionId);
    List<Categoria> findByEdicionIdAndActiva(Long edicionId, Boolean activa);


    // 🔎 Validar que no se repita combinación en la misma edición
    Optional<Categoria> findByEdicionAndAnioNacimientoAndNivel(
            EdicionTorneo edicion,
            Integer anioNacimiento,
            NivelCompetencia nivel
    );

    // 🔎 Buscar por organizador (multi-organizador)
    List<Categoria> findByOrganizadorId(Long organizadorId);

    // 🔎 Buscar por organizador y edición
    List<Categoria> findByOrganizadorIdAndEdicionId(Long organizadorId, Long edicionId);
    List<Categoria> findByOrganizadorIdAndActiva(Long organizadorId, Boolean activa);
}