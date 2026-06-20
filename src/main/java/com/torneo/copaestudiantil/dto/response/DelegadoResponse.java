package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.EstadoDelegado;
import lombok.*;

/** Datos del delegado para el panel admin (incluye el código de invitación). */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DelegadoResponse {
    private Long id;
    private Long equipoId;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String email;
    private String codigoInvitacion;
    private EstadoDelegado estado;
    private Boolean activo;
}
