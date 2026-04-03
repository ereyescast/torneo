package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "categorias",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "organizador_id",
                        "edicion_id",
                        "anio_nacimiento",
                        "nivel"
                })
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 Multi-organizador
    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    // 📅 Relación con Edición
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    // 🎂 Año de nacimiento (ej: 2016)
    @Column(name = "anio_nacimiento", nullable = false)
    private Integer anioNacimiento;

    // 🏆 Nivel
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelCompetencia nivel;

    // ⚽ Modalidad
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModalidadJuego modalidad;

    // 🔢 Cupos máximos por equipo (opcional futuro)
    @Column(name = "max_jugadores_por_equipo")
    private Integer maxJugadoresPorEquipo;

    // 🔄 Activa o no
    @Column(nullable = false)
    private Boolean activa = true;

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