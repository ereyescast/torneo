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
public class Torneos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    private Date fechaInicio;

    @Column(nullable = false)
    @NotBlank
    private Date fechaFin;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String edicion;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String sede;
}
