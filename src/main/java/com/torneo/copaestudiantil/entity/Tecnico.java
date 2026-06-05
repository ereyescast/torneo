package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tecnicos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tecnico_organizador_documento",
                        columnNames = {"organizador_id", "numero_documento"})
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Multi-tenancy: cada organizador tiene su propia lista de técnicos. */
    @Column(name = "organizador_id", nullable = false)
    private Long organizadorId;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidosPaterno;

    @Column(length = 100)
    private String apellidosMaterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 30)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(length = 50)
    private String nacionalidad;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "tecnico")
    @Builder.Default
    private List<TecnicoEquipoEdicion> asignaciones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
