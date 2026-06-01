package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoPago;
import com.torneo.copaestudiantil.entity.TipoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoEquipoResponse {
    private Long id;
    private Long organizadorId;
    private EquipoResponse equipo;
    private EdicionTorneoResponse edicion;
    private TipoPago tipoPago;
    private EstadoPago estado;
    private BigDecimal monto;
    private LocalDate fechaLimite;
    private LocalDate fechaPago;
    private String referenciaPago;
    private String observacion;
    private Long partidoId;
}
