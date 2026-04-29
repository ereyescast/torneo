package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "partidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 Multi-organizador
    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    // 📅 Edición
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    // 🏆 Categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // 🏟 Sede (aunque el equipo tenga sede fija, puede variar en finales)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    // 🏠 Equipo Local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_local_id", nullable = false)
    private Equipo equipoLocal;

    // 🛫 Equipo Visitante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_visitante_id", nullable = false)
    private Equipo equipoVisitante;

    // 📆 Fecha y hora del partido
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // ⚽ Goles
    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    // 🏁 Fase del torneo (GRUPOS, SEMIFINAL_ORO, FINAL_ORO, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "fase", nullable = false, length = 30)
    private FasePartido fase;

    // 👥 Grupo al que pertenece (solo aplica cuando fase = GRUPOS; null en eliminatorias)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    // 🔄 Estado del partido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPartido estado;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPartido.PROGRAMADO;
        }
        if (this.fase == null) {
            this.fase = FasePartido.GRUPOS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}