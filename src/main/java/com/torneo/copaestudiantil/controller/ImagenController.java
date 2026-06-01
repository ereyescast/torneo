package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.TecnicoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Tag(name = "15. Imágenes", description = "Descarga de fotos de jugadores y técnicos")
@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final TecnicoRepository tecnicoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Operation(
            summary = "Obtener foto de técnico",
            description = "Retorna la imagen del técnico en formato binario (JPG o PNG)"
    )
    @GetMapping("/tecnicos/{id}")
    public ResponseEntity<Resource> obtenerImagenTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long id) {

        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        if (tecnico.getProfileImage() == null)
            throw new ResourceNotFoundException("El técnico no tiene imagen");

        try {
            Path path = Paths.get(uploadDir)
                    .resolve(tecnico.getProfileImage()).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists())
                throw new ResourceNotFoundException("Imagen no encontrada en el servidor");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error al cargar la imagen");
        }
    }
}
