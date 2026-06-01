package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registra los pagos de un equipo en el torneo.
 * El organizador confirma manualmente cada pago.
 *
 * XIV — Costos Copa Estudiantil:
 *   F7: S/.350 inscripción + S/.45 arbitraje
 *   F8: S/.400 inscripción + S/.60 arbitraje
 *   F9: S/.500 inscripción + S/.70 arbitraje
 *
 * ART. 16a: Multa S/.50 por WO
 * ART. 40:  Multa S/.50 por reclamo
 * ART. 11:  Cancelación total hasta la 2da fecha
 */
@Entity
@Table(name = "pagos_equipos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 30)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    /** Fecha límite de pago según las bases */
    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    /** Fecha en que el organizador confirmó el pago */
    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    /**
     * Referencia del pago (BCP, Yape, efectivo).
     * Ej: "Yape 970498558 - Operación #12345"
     */
    @Column(name = "referencia_pago", length = 200)
    private String referenciaPago;

    /** Observación del organizador */
    @Column(length = 300)
    private String observacion;

    /**
     * Partido relacionado al pago (solo para ARBITRAJE y MULTA_WO).
     * null para INSCRIPCION.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id")
    private Partido partido;

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
