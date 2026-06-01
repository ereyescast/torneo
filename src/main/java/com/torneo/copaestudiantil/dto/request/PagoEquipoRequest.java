package com.torneo.copaestudiantil.dto.request;

import com.torneo.copaestudiantil.entity.TipoPago;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoEquipoRequest {

    @NotNull
    private Long organizadorId;

    @NotNull
    private Long equipoId;

    @NotNull
    private Long edicionId;

    @NotNull
    private TipoPago tipoPago;

    @NotNull
    private BigDecimal monto;

    private LocalDate fechaLimite;
    private String referenciaPago;
    private String observacion;

    /** Solo para ARBITRAJE y MULTA_WO */
    private Long partidoId;
}
