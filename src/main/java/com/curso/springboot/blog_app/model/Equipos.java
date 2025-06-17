package com.curso.springboot.blog_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
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

    @Size(max = 300)
    private String profile_image;


    @OneToOne
    @JoinColumn(name = "tecnico_id" , nullable = false, unique = true)
    private Tecnicos tecnico;

    @OneToOne(targetEntity = Torneos.class, cascade = CascadeType.PERSIST)
    private Torneos torneo;

}
