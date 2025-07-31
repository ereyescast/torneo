package com.torneo.copaestudiantil.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String nombres;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String apellidosPaterno;

    @Column(nullable = false, length = 100)
    private String apellidosMaterno;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String tipoDocumento;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    private String numeroDocumento;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String nacionalidad;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNac;

    @Column(length = 300)
    private String profile_image;
}
