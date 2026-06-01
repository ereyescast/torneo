package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Define una cancha disponible en un fixture específico.
 *
 * La misma cancha física puede transformarse durante el día:
 *   MAÑANA:      [1][2][3][4][5][6] → Fútbol 7 (×6) 08:00-13:00
 *   TARDE:       [7][8]             → Fútbol 9 (×2) 13:00-18:00
 *   NOCHE:       [9]                → Fútbol 11 (×1) 18:00-22:00
 *
 * El organizador configura cuántas canchas hay por modalidad ese día.
 * El sistema calcula cuántos partidos caben: (horaFin - horaInicio) / duracionPartidoMin
 */
@Entity
@Table(name = "configuracion_canchas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracionCancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    /**
     * Nombre de la cancha tal como aparece en el fixture PDF.
     * Ej: "Campo 1", "Campo 7", "Campo 9"
     */
    @Column(name = "nombre_cancha", nullable = false, length = 50)
    private String nombreCancha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadJuego modalidad;

    /** Hora de inicio de esta cancha ese día */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    /** Hora de fin de esta cancha ese día */
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * Duración de cada partido en minutos.
     * Default: viene del enum ModalidadJuego (40 min para F7 y F9).
     * El organizador puede cambiarlo si las bases del torneo son diferentes.
     *
     * ART. 27 Copa Estudiantil: F7 = 40 min, F9 = 40 min
     */
    @Column(name = "duracion_partido_min", nullable = false)
    private Integer duracionPartidoMin;

    /**
     * Calcula cuántos partidos caben en esta cancha.
     * (horaFin - horaInicio) en minutos / duracionPartidoMin
     */
    @Transient
    public int getCapacidadPartidos() {
        int totalMinutos = horaFin.toSecondOfDay() / 60 - horaInicio.toSecondOfDay() / 60;
        return totalMinutos / duracionPartidoMin;
    }
}
