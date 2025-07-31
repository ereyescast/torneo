package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Partidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    private Date fecha;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int golesLocal;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 3)
    private int golesVisitante;

    @Size(max = 300)
    private String comentario;

    @OneToOne(targetEntity = Equipos.class, cascade = CascadeType.PERSIST)
    private Equipos equipo_local;

    @OneToOne(targetEntity = Equipos.class, cascade = CascadeType.PERSIST)
    private Equipos equipo_visitante;

    @ManyToOne(targetEntity = Torneos.class, cascade = CascadeType.PERSIST)
    private Torneos torneo;

    @OneToMany(targetEntity = Arbitros.class, fetch = FetchType.LAZY, mappedBy = "partidos")
    private List<Arbitros> arbitros;
}
