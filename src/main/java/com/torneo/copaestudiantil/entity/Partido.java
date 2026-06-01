package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "partidos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_local_id", nullable = false)
    private Equipo equipoLocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_visitante_id", nullable = false)
    private Equipo equipoVisitante;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "fase", nullable = false, length = 30)
    private FasePartido fase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPartido estado;

    /**
     * Cancha donde se juega el partido.
     * Ej: "Campo 1", "Campo 7", "Campo 9"
     * Asignado automáticamente al generar el fixture.
     * El organizador puede cambiarlo via reprogramar.
     */
    @Column(name = "cancha", length = 50)
    private String cancha;

    /**
     * Fixture al que pertenece este partido.
     * Null si el partido fue creado manualmente sin fixture.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id")
    private Fixture fixture;

    /**
     * Flag de idempotencia — evita doble actualización de tabla.
     * Se marca true cuando la tabla de posiciones ya fue actualizada.
     * Si se llama actualizarTabla dos veces, la segunda es ignorada.
     */
    @Column(name = "tabla_actualizada", nullable = false)
    @Builder.Default
    private Boolean tablaActualizada = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoPartido.PROGRAMADO;
        if (this.fase == null) this.fase = FasePartido.GRUPOS;
        if (this.tablaActualizada == null) this.tablaActualizada = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
