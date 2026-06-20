package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** El delegado se registra con el código que le dio el organizador. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistroDelegadoRequest {

    @NotBlank(message = "El código de invitación es obligatorio")
    private String codigo;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String apellidosPaterno;

    private String apellidosMaterno;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
