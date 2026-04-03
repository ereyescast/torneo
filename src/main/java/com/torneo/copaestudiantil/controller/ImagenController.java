package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final TecnicoRepository tecnicoRepository;

    @GetMapping("/tecnicos/{id}")
    public ResponseEntity<Resource> obtenerImagenTecnico(@PathVariable Long id) {

        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        if (tecnico.getProfileImage() == null) {
            throw new ResourceNotFoundException("El técnico no tiene imagen");
        }

        try {
            Path path = Paths.get("." + tecnico.getProfileImage());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Imagen no encontrada");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error al cargar la imagen");
        }
    }
}