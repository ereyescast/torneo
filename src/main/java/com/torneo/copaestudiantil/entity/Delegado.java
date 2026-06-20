package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Delegado de un equipo. El organizador lo invita generando un código; el delegado
 * se registra con ese código y a partir de ahí gestiona los jugadores de SU equipo.
 *
 * Multi-tenant: pertenece a un organizadorId y está atado a un equipoId concreto.
 * El nombre se llena cuando el delegado se registra (antes está PENDIENTE).
 */
@Entity
@Table(name = "delegados",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo_invitacion"),
                @UniqueConstraint(columnNames = "equipo_id") // un delegado por equipo
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Delegado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del delegado (se completan al registrarse)
    @Column(length = 100)
    private String nombres;

    @Column(name = "apellidos_paterno", length = 100)
    private String apellidosPaterno;

    @Column(name = "apellidos_materno", length = 100)
    private String apellidosMaterno;

    @Column(length = 150)
    private String email;

    // Vínculos
    @Column(name = "equipo_id", nullable = false)
    private Long equipoId;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    /** Usuario de login asociado. null mientras el delegado no se ha registrado. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    // Invitación
    @Column(name = "codigo_invitacion", nullable = false, length = 20)
    private String codigoInvitacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EstadoDelegado estado = EstadoDelegado.PENDIENTE;

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
        this.fechaActualizacion = this.fechaCreacion;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
