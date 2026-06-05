package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "jugadores",
        uniqueConstraints = {
                // Documento único POR ORGANIZADOR (no global).
                // Así Juanje puede registrar a un jugador aunque Miguel ya lo tenga.
                @UniqueConstraint(
                        name = "uk_jugador_organizador_documento",
                        columnNames = {"organizador_id", "numero_documento"})
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Multi-tenancy: cada organizador tiene su propia lista de jugadores. */
    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 100)
    private String apellidoMaterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 30)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 50)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 50)
    private String nacionalidad;

    /**
     * Género del jugador — opcional.
     * ART. 22: las niñas en fútbol femenino competitivo
     * pueden jugar una categoría arriba.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Genero genero;

    @Column(name = "profile_image", length = 300)
    private String profileImage;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
