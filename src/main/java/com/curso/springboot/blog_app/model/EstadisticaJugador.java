package com.curso.springboot.blog_app.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class EstadisticaJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String estado;
    private int goles;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private int penales;
    private int auotogoles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;
    private int tirosLibres;
    private int tirosDeEsquina;
}
