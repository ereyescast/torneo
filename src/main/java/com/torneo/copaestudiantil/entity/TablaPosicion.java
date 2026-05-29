package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tabla_posiciones",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "equipo_id", "edicion_id", "categoria_id", "grupo_id"
                })
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TablaPosicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lock optimista — Hibernate incrementa este valor en cada UPDATE.
     * Si dos transacciones leen el mismo registro y ambas intentan guardarlo,
     * la segunda lanza OptimisticLockException evitando doble suma de goles/puntos.
     * Fix para: riesgo de concurrencia en actualizarTablaAlFinalizarPartido().
     */
    @Version
    private Long version;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    private Integer partidosJugados   = 0;
    private Integer partidosGanados   = 0;
    private Integer partidosEmpatados = 0;
    private Integer partidosPerdidos  = 0;
    private Integer golesFavor        = 0;
    private Integer golesContra       = 0;
    private Integer diferenciaGol     = 0;
    private Integer puntos            = 0;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
