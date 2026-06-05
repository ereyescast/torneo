package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ediciones",
        uniqueConstraints = {
                // Una edición es única por organizador + nombre + fecha de inicio.
                // Permite: "Copa Kids" 2025 y "Copa Kids" 2026 (distinta fecha)
                // Permite: "Copa Kids" y "Copa Kids 3era" misma fecha (distinto nombre)
                // Bloquea: duplicado exacto accidental (doble/triple clic)
                @UniqueConstraint(
                        name = "uk_edicion_organizador_nombre_fecha",
                        columnNames = {"organizador_id", "nombre", "fecha_inicio"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdicionTorneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "edicion")
    @Builder.Default
    private List<TecnicoEquipoEdicion> asignacionesTecnicos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
