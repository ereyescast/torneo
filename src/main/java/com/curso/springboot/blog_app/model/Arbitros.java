package com.curso.springboot.blog_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Arbitros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String nombres;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String apellidosPaterno;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String apellidosMaterno;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String tipoDocumento;

    @Column(nullable = false, unique = true)
    @Size(max = 30)
    private String numeroDocumento;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String nacionalidad;

    @Column(nullable = false, unique = true)
    @NotBlank
    private Date fechaNac;

    @Size(max = 300)
    private String profile_image;
}
