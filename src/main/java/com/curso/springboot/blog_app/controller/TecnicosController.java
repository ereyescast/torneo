package com.curso.springboot.blog_app.controller;


import com.curso.springboot.blog_app.model.Tecnicos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/torneo")
public class TecnicosController {

    @GetMapping
    public ResponseEntity<Tecnicos> getTecnicos(){
        return ResponseEntity.status(HttpStatus.CREATED).body(new Tecnicos(1,"Anthony","Venezolano",""));
    }
}
