package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstadisticaJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String estado;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int goles;

    @Column(nullable = false)
    @NotBlank
    private LocalDateTime horaInicio;

    @Column(nullable = false)
    @NotBlank
    private LocalDateTime horaFin;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int penales;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int autogoles;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 5)
    private int asistencias;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int tarjetasAmarillas;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int tarjetasRojas;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 5)
    private int tirosLibres;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 5)
    private int tirosDeEsquina;

    @OneToOne(targetEntity = Jugadores.class, cascade = CascadeType.PERSIST)
    private Equipos jugador;

    @OneToOne(targetEntity = Partidos.class, cascade = CascadeType.PERSIST)
    private Equipos partido;
}
