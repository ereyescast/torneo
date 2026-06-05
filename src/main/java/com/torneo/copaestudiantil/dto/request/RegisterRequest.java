package com.torneo.copaestudiantil.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    /**
     * Nombre del organizador/torneo que este usuario va a gestionar.
     * Ej: "Bundesliga Kids Perú", "Copa Estudiantil Callao".
     * Al registrarse, el sistema crea el organizador y vincula el usuario.
     */
    @NotBlank(message = "El nombre del organizador es obligatorio")
    private String nombreOrganizador;
}
