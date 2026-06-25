package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.service.ImagenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Descarga de imágenes. El controller solo recibe la petición y delega en
 * ImagenService — la resolución del archivo en disco y las validaciones
 * viven en el service.
 */
@Tag(name = "15. Imágenes", description = "Descarga de fotos de jugadores y técnicos")
@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

    @Operation(
            summary = "Obtener foto de técnico",
            description = "Retorna la imagen del técnico en formato binario (JPG o PNG)"
    )
    @GetMapping("/tecnicos/{id}")
    public ResponseEntity<Resource> obtenerImagenTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long id) {

        Resource imagen = imagenService.obtenerImagenTecnico(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(imagen);
    }

    @Operation(
            summary = "Obtener foto de jugador",
            description = "Retorna la imagen del jugador en formato binario (JPG o PNG)"
    )
    @GetMapping("/jugadores/{id}")
    public ResponseEntity<Resource> obtenerImagenJugador(
            @Parameter(description = "ID del jugador") @PathVariable Long id) {

        Resource imagen = imagenService.obtenerImagenJugador(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(imagen);
    }
}
