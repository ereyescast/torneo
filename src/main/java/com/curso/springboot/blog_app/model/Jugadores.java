package com.curso.springboot.blog_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Jugadores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private int dni;
    private Date fechaNac;
    private String posicion;
    private int numCamiseta;
    private String profile_image;
}
