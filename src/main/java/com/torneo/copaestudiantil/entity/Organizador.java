package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizadores",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "nombre"),
                @UniqueConstraint(columnNames = "codigo_publico")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Organizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    /**
     * Nombre normalizado para búsqueda (sin tildes, minúsculas).
     * Lo llena la capa de servicio (AuthServiceImpl) al crear/editar.
     * Permite que "sabados" encuentre "Sábados" en el directorio.
     */
    @Column(name = "nombre_busqueda", length = 150)
    private String nombreBusqueda;

    /**
     * Slug único para las URLs públicas que se comparten por WhatsApp.
     * Ej: "copa-estudiantil-callao", "bundesliga-kids-peru".
     * Se genera automáticamente del nombre al registrarse.
     * Los padres acceden a: /api/public/{codigoPublico}/tabla
     */
    @Column(name = "codigo_publico", nullable = false, length = 100, unique = true)
    private String codigoPublico;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 300)
    private String direccion;

    /** Logo del organizador para mostrar en la vista pública */
    @Column(name = "logo_url", length = 300)
    private String logoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Controla si el torneo aparece en el directorio público (la lista
     * que exploran los padres). true = visible para todos; false = solo
     * accesible si conoces el código (torneo privado).
     */
    @Column(name = "visible_en_directorio", nullable = false)
    @Builder.Default
    private Boolean visibleEnDirectorio = true;

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
