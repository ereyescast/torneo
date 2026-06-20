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

    OrganizadorPublicoResponse obtenerOrganizador(String codigoPublico);
    List<EdicionResumenResponse> listarEdiciones(String codigoPublico);
    List<CategoriaResumenResponse> listarCategorias(String codigoPublico, Long edicionId);
    List<TablaPosicionResponse> obtenerTabla(String codigoPublico, Long edicionId, Long categoriaId);
    List<PartidoPublicoResponse> listarPartidos(String codigoPublico, Long edicionId, Long categoriaId);
    List<TorneoDirectorioResponse> listarTorneos(String q);

    /** Sedes activas del torneo (dónde se juega). */
    List<SedePublicaResponse> listarSedes(String codigoPublico);

    /**
     * Ranking de goleadores. fase es opcional (null = todas las fases).
     */
    List<GoleadorResponse> rankingGoleadores(
            String codigoPublico, Long edicionId, Long categoriaId, String fase);

    /**
     * Ranking de tarjetas (amarillas y rojas). fase es opcional.
     */
    List<TarjetaResponse> rankingTarjetas(
            String codigoPublico, Long edicionId, Long categoriaId, String fase);

    /** Plantel (técnico + jugadores activos) de un equipo. */
    PlantelResponse listarPlantel(String codigoPublico, Long equipoId);
}
