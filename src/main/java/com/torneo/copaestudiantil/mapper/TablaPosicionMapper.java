package com.torneo.copaestudiantil.mapper;

import com.torneo.copaestudiantil.dto.response.TablaPosicionResponse;
import com.torneo.copaestudiantil.entity.TablaPosicion;
import org.springframework.stereotype.Component;

@Component
public class TablaPosicionMapper {

    public TablaPosicionResponse toResponse(TablaPosicion tabla) {

        return TablaPosicionResponse.builder()
                .equipoId(tabla.getEquipo().getId())
                .nombreEquipo(tabla.getEquipo().getNombre())
                .partidosJugados(tabla.getPartidosJugados())
                .partidosGanados(tabla.getPartidosGanados())
                .partidosEmpatados(tabla.getPartidosEmpatados())
                .partidosPerdidos(tabla.getPartidosPerdidos())
                .golesFavor(tabla.getGolesFavor())
                .golesContra(tabla.getGolesContra())
                .diferenciaGol(tabla.getDiferenciaGol())
                .puntos(tabla.getPuntos())
                .build();
    }
}