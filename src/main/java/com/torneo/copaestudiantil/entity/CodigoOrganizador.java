package com.torneo.copaestudiantil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Código de invitación que el ADMIN de plataforma genera para que un nuevo
 * organizador se registre. Es de UN SOLO USO: al registrarse, pasa a USADO.
 */
@Entity
@Table(name = "codigos_organizador",
        uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodigoOrganizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String codigo;

    /** Nota opcional para que el admin recuerde a quién es (ej. "Para Liga Sur"). */
    @Column(length = 150)
    private String nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EstadoCodigoOrganizador estado = EstadoCodigoOrganizador.DISPONIBLE;

    /** Organizador creado con este código (se llena al usarse). */
    @Column(name = "organizador_id")
    private Long organizadorId;

    /** Email del usuario que usó el código (trazabilidad). */
    @Column(name = "email_usado", length = 150)
    private String emailUsado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
