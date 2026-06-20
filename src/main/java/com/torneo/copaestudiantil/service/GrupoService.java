package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.GrupoEquipoResponse;
import com.torneo.copaestudiantil.dto.response.GrupoResponse;

import java.util.List;

/**
 * Lógica de negocio de los grupos de la fase de grupos.
 *
 * Toda la lógica (búsqueda, validación de pertenencia multi-tenant,
 * persistencia, inicialización de tabla) vive aquí. El controller solo
 * delega — respetando la arquitectura en capas del proyecto.
 */
public interface GrupoService {

    /** Lista los grupos activos de una edición + categoría del organizador actual. */
    List<GrupoResponse> listar(Long edicionId, Long categoriaId);

    /** Obtiene un grupo por su ID (valida que pertenezca al organizador). */
    GrupoResponse buscarPorId(Long id);

    /** Crea un grupo (ej: "Grupo A") en una edición + categoría. */
    GrupoResponse crear(Long edicionId, Long categoriaId, String nombre);

    /**
     * Agrega un equipo al grupo e inicializa su fila en la tabla de posiciones.
     */
    GrupoEquipoResponse agregarEquipo(Long grupoId, Long equipoId);

    /** Lista los equipos de un grupo. */
    List<GrupoEquipoResponse> listarEquipos(Long grupoId);

    /** Desactiva un grupo (soft delete). */
    void desactivar(Long id);
}
