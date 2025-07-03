package com.curso.springboot.blog_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Equipos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String categoria;

    @NotBlank
    @Size(max = 100)
    private String distrito;

    @NotBlank
    @Size(max = 100)
    private String provincia;

    @NotBlank
    @Size(max = 100)
    private String departamento;

    private boolean esPeru;

    @NotBlank
    @Size(max = 100)
    private String ciudadExterior;

    @NotBlank
    @Size(max = 100)
    private String pais;

    @Size(max = 500)
    private String profile_image;

    @OneToOne(targetEntity = Tecnicos.class, cascade = CascadeType.PERSIST)
    private Tecnicos tecnico;

    @ManyToOne(targetEntity = Torneos.class, cascade = CascadeType.PERSIST)
    private Torneos torneo;

    @OneToMany(targetEntity = Jugadores.class, fetch = FetchType.LAZY, mappedBy = "equipo")
    private List<Jugadores> jugadoresList;

}
