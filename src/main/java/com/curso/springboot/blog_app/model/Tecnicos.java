package com.curso.springboot.blog_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Tecnicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nombres;

    private String apellidosPaterno;
    private String apellidosMaterno;
    private int dni;

    @Column(nullable = false)
    private String nacionalidad;

    @Column(nullable = false)
    private String profile_image;




}
