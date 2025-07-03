package com.curso.springboot.blog_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Arbitros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String nombres;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String apellidosPaterno;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String apellidosMaterno;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String tipoDocumento;

    @Column(nullable = false, unique = true)
    @Size(max = 50)
    private String numeroDocumento;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String nacionalidad;

    @Column(nullable = false, unique = true)
    @NotBlank
    private Date fechaNac;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String tipo;

    @Size(max = 500)
    private String profile_image;

    @ManyToOne(targetEntity = Partidos.class, cascade = CascadeType.PERSIST)
    private Partidos partidos;
}
