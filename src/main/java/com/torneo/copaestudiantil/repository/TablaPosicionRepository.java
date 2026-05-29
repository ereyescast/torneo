package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.TablaPosicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TablaPosicionRepository extends JpaRepository<TablaPosicion, Long>,
        JpaSpecificationExecutor<TablaPosicion> {

    /**
     * Busca la fila de un equipo en la tabla de su grupo específico.
     * Usado al actualizar resultados de partidos de fase GRUPOS.
     */
    Optional<TablaPosicion> findByEquipoIdAndEdicionIdAndCategoriaIdAndGrupoId(
            Long equipoId,
            Long edicionId,
            Long categoriaId,
            Long grupoId
    );

    /**
     * Compatibilidad: busca sin filtrar por grupo (para partidos de fase no-grupos
     * o cuando la tabla fue inicializada sin grupo asignado).
     */
    Optional<TablaPosicion> findByEquipoIdAndEdicionIdAndCategoriaIdAndGrupoIsNull(
            Long equipoId,
            Long edicionId,
            Long categoriaId
    );

    /**
     * Tabla ordenada de un grupo concreto.
     */
    List<TablaPosicion> findByGrupoIdOrderByPuntosDescDiferenciaGolDescGolesFavorDesc(
            Long grupoId
    );

    /**
     * Tabla ordenada de una edición+categoría completa (sin filtrar por grupo).
     */
    List<TablaPosicion> findByEdicionIdAndCategoriaIdOrderByPuntosDescDiferenciaGolDescGolesFavorDesc(
            Long edicionId,
            Long categoriaId
    );
}