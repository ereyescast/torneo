package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoCodigoOrganizador;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodigoOrganizadorResponse {
    private Long id;
    private String codigo;
    private String nota;
    private EstadoCodigoOrganizador estado;
    private Long organizadorId;
    private String emailUsado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUso;
}
