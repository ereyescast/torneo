package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.ConfiguracionCanchaRequest;
import com.torneo.copaestudiantil.dto.request.FixtureRequest;
import com.torneo.copaestudiantil.dto.request.GenerarFixtureRequest;
import com.torneo.copaestudiantil.dto.response.FixtureResponse;
import com.torneo.copaestudiantil.dto.response.PartidoResponse;

import java.util.List;

public interface FixtureService {

    /** Crea un fixture en estado BORRADOR */
    FixtureResponse crear(FixtureRequest request);

    /** Obtiene un fixture por ID */
    FixtureResponse obtenerPorId(Long id);

    /** Lista fixtures de una edición */
    List<FixtureResponse> listarPorEdicion(Long edicionId);

    /**
     * Configura las canchas disponibles para un fixture.
     * Reemplaza la configuración anterior si ya existía.
     */
    FixtureResponse configurarCanchas(Long fixtureId, List<ConfiguracionCanchaRequest> canchas);

    /**
     * Genera los partidos automáticamente distribuyéndolos en las canchas configuradas.
     *
     * Algoritmo:
     * 1. Lee las ConfiguracionCanchas del fixture
     * 2. Obtiene los grupos de la categoría
     * 3. Genera todos los enfrentamientos (todos contra todos)
     * 4. Distribuye en canchas por modalidad respetando horarios
     * 5. Asigna cancha y hora a cada partido
     *
     * Lanza error si:
     * - El fixture ya tiene partidos generados
     * - No hay canchas configuradas
     * - No hay grupos con suficientes equipos
     */
    List<PartidoResponse> generarPartidos(Long fixtureId, GenerarFixtureRequest request);

    /** Cambia el estado del fixture a PUBLICADO */
    FixtureResponse publicar(Long fixtureId);

    /** Cambia el estado del fixture a FINALIZADO */
    FixtureResponse finalizar(Long fixtureId);

    /** Elimina el fixture y sus partidos si está en BORRADOR */
    void eliminar(Long fixtureId);
}
