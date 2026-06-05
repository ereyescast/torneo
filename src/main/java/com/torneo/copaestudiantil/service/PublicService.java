package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.*;

import java.util.List;

/**
 * Servicio de la VISTA PÚBLICA (padres y público, sin login).
 *
 * Todos los métodos reciben el codigoPublico (slug) del organizador,
 * lo resuelven a un organizadorId internamente, y devuelven SOLO datos
 * de ese organizador. Es de solo lectura.
 */
public interface PublicService {

    /** Información básica del organizador/torneo (nombre, logo) por su slug. */
    OrganizadorPublicoResponse obtenerOrganizador(String codigoPublico);

    /** Ediciones activas del organizador (los torneos disponibles para ver). */
    List<EdicionResumenResponse> listarEdiciones(String codigoPublico);

    /** Categorías de una edición. */
    List<CategoriaResumenResponse> listarCategorias(String codigoPublico, Long edicionId);

    /** Tabla de posiciones de una edición + categoría. */
    List<TablaPosicionResponse> obtenerTabla(String codigoPublico, Long edicionId, Long categoriaId);

    /** Partidos de una edición + categoría (fixture y resultados). */
    List<PartidoPublicoResponse> listarPartidos(String codigoPublico, Long edicionId, Long categoriaId);
}
