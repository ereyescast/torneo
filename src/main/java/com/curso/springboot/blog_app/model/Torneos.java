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

    @OneToMany(targetEntity = Partidos.class, fetch = FetchType.LAZY, mappedBy = "torneo")
    private List<Partidos> partidosList;

    @OneToMany(targetEntity = Equipos.class, fetch = FetchType.LAZY, mappedBy = "torneo")
    private List<Equipos> equiposList;
}
