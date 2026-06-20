package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.TecnicoEquipoEdicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TecnicoEquipoEdicionRepository
        extends JpaRepository<TecnicoEquipoEdicion, Long> {

    /** ¿Ya existe esta asignación exacta (técnico-equipo-edición)? */
    boolean existsByTecnicoIdAndEquipoIdAndEdicionId(
            Long tecnicoId, Long equipoId, Long edicionId);

    /**
     * Técnico ACTIVO de un equipo (trae el Tecnico para la vista pública).
     * Un equipo pertenece a una sola edición, así que basta el equipoId.
     */
    @Query("""
            SELECT a FROM TecnicoEquipoEdicion a
            JOIN FETCH a.tecnico
            WHERE a.equipo.id = :equipoId
              AND a.activo = true
            ORDER BY a.fechaInicio DESC
            """)
    Optional<TecnicoEquipoEdicion> findTecnicoActivoDeEquipo(@Param("equipoId") Long equipoId);
}
