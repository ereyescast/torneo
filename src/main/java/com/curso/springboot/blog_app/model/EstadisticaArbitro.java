package com.curso.springboot.blog_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstadisticaArbitro {

    @OneToOne(targetEntity = Arbitros.class, cascade = CascadeType.PERSIST)
    private Arbitros arbitro;

    @OneToOne(targetEntity = Partidos.class, cascade = CascadeType.PERSIST)
    private Partidos partido;

    @Size(max = 20)
    private String tipoArbitro;
}
