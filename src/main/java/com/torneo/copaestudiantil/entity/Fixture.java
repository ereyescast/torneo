package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agrupa todos los partidos de un sábado específico del torneo.
 *
 * Un Fixture representa una fecha completa (ej: Sábado 17 de Enero 2026).
 * Contiene las canchas disponibles ese día y los partidos programados.
 *
 * Flujo:
 * 1. Crear Fixture en BORRADOR
 * 2. Configurar canchas disponibles
 * 3. Generar partidos automáticamente
 * 4. Ajustar manualmente si necesita
 * 5. Publicar → PDF para WhatsApp
 */
@Entity
@Table(name = "fixtures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fixture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria; // null = fixture para todas las categorías del día

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    /** Fecha del sábado (ej: 2026-01-17) */
    @Column(name = "fecha_torneo", nullable = false)
    private LocalDate fechaTorneo;

    /** Número de fecha del torneo (1, 2, 3, 4, 5, 6) */
    @Column(name = "numero_fecha", nullable = false)
    private Integer numeroFecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoFixture estado = EstadoFixture.BORRADOR;

    /** Canchas disponibles para este fixture */
    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConfiguracionCancha> canchas = new ArrayList<>();

    /** Partidos generados para este fixture */
    @OneToMany(mappedBy = "fixture")
    @Builder.Default
    private List<Partido> partidos = new ArrayList<>();

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
