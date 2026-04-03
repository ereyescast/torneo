package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.entity.NivelCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // 🔎 Buscar categorías por edición
    List<Categoria> findByEdicion(EdicionTorneo edicion);

    // 🔎 Buscar categorías por edición ID (más práctico a veces)
    List<Categoria> findByEdicionId(Long edicionId);

    // 🔎 Buscar categorías activas
    List<Categoria> findByActivaTrue();

    // 🔎 Buscar categorías activas por edición
    List<Categoria> findByEdicionIdAndActivaTrue(Long edicionId);

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
}