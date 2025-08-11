package com.torneo.copaestudiantil.controller;


import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tecnicos")
public class TecnicosController {

    @Autowired
    private TecnicoService tecnicoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarTecnico(@RequestBody Tecnico tecnico){
        Tecnico nuevoTecnico = tecnicoService.registrarTecnico(tecnico);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTecnico);
    }

    @GetMapping
    public ResponseEntity<List<Tecnico>> listarTecnico (){
        List<Tecnico> tecnicos = tecnicoService.listarTecnicos();
        return ResponseEntity.ok(tecnicos);
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre){
        Optional<Tecnico> tecnico = tecnicoService.buscarPorNombre(nombre);
        return tecnico.isPresent()?ResponseEntity.ok(tecnico.get()):ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tecnico no encontrado");
    }

    @GetMapping("/buscar/id/{idTecnico}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable Long idTecnico){
        Optional<Tecnico> tecnico = tecnicoService.buscarPorId(idTecnico);
        return tecnico.isPresent()?ResponseEntity.ok(tecnico.get()):ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tecnico no encontrado");
    }

    @PutMapping("/actualizar/{idTecnico}")
    public ResponseEntity<?> actualizarTecnico(@PathVariable Long idTecnico, @RequestBody Tecnico tecnico){
        try {
            Tecnico tecnicoActualizado = new Tecnico();
            tecnicoActualizado.setId(idTecnico);
            tecnicoActualizado.setNombres(tecnico.getNombres());
            tecnicoActualizado.setApellidosPaterno(tecnico.getApellidosPaterno());
            tecnicoActualizado.setApellidosMaterno(tecnico.getApellidosMaterno());
            tecnicoActualizado.setFechaNac(tecnico.getFechaNac());
            tecnicoActualizado.setTipoDocumento(tecnico.getTipoDocumento());
            tecnicoActualizado.setNumeroDocumento(tecnico.getNumeroDocumento());
            tecnicoActualizado.setNacionalidad(tecnico.getNacionalidad());
            tecnicoActualizado.setProfile_image(tecnico.getProfile_image());

            Tecnico tecnicoFinal = tecnicoService.actualizarTecnico(idTecnico, tecnico);
            return ResponseEntity.status(HttpStatus.OK).body(tecnicoFinal);

                    //tecnicoService.actualizarTecnico(idTecnico, tecnico);

        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @DeleteMapping("/{idTecnico}")
    public ResponseEntity<?> eliminarTecnico(@PathVariable Long idTecnico){
        try{
            tecnicoService.eliminarTecnico(idTecnico);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }

    }
}
