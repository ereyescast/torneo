package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoFixture;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FixtureResponse {

    private Long id;
    private Long organizadorId;
    private EdicionTorneoResponse edicion;
    private CategoriaResponse categoria;
    private SedeResponse sede;
    private LocalDate fechaTorneo;
    private Integer numeroFecha;
    private EstadoFixture estado;
    private List<ConfiguracionCanchaResponse> canchas;
    private Integer totalPartidosGenerados;
}
