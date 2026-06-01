package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registra la suspensión de un jugador por tarjeta roja.
 *
 * ART. 23 — Copa Estudiantil Cup Callao:
 * "En caso de expulsión el jugador será suspendido una fecha."
 * "Las tarjetas amarillas no son acumulables para los siguientes partidos."
 *
 * Flujo:
 * 1. Árbitro registra tarjeta roja en EstadisticaJugador
 * 2. Sistema crea automáticamente un registro en SuspensionJugador
 * 3. Al crear un partido, se valida que ningún jugador esté suspendido en esa fecha
 * 4. El organizador puede levantar la suspensión manualmente (casos especiales)
 */
@Entity
@Table(name = "suspensiones_jugadores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuspensionJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edicion_id", nullable = false)
    private EdicionTorneo edicion;

    /** Partido en el que recibió la tarjeta roja */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_origen_id", nullable = false)
    private Partido partidoOrigen;

    /** Número de fecha en que recibió la roja */
    @Column(name = "fecha_origen", nullable = false)
    private Integer fechaOrigen;

    /** Número de fecha en que cumple la suspensión (fechaOrigen + 1) */
    @Column(name = "fecha_suspension", nullable = false)
    private Integer fechaSuspension;

    /** true = suspensión activa, false = cumplida o levantada */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /** Motivo opcional (ej: "Expulsión directa por agresión - Art. 23") */
    @Column(length = 300)
    private String motivo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
