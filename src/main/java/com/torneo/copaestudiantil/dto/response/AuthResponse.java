package com.torneo.copaestudiantil.dto.response;

import com.torneo.copaestudiantil.entity.RolUsuario;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String tipo;
    private Long usuarioId;
    private String nombre;
    private String email;
    private RolUsuario rol;
}
