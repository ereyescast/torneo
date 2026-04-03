package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import com.torneo.copaestudiantil.service.TecnicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoService tecnicoService;

    // ================================
    // CREATE
    // ================================
    @PostMapping
    public ResponseEntity<TecnicoResponse> registrar(
            @Valid @RequestBody TecnicoRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tecnicoService.registrar(request));
    }

    // ================================
    // READ - LIST WITH FILTERS
    // ================================
    @GetMapping
    public ResponseEntity<Page<TecnicoResponse>> buscar(
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) String nacionalidad,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(
                tecnicoService.buscar(nombres, numeroDocumento, nacionalidad, pageable)
        );
    }

    // ================================
    // READ - BY ID
    // ================================
    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicoService.obtenerPorId(id));
    }

    // ================================
    // UPDATE
    // ================================
    @PutMapping("/{id}")
    public ResponseEntity<TecnicoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TecnicoRequest request) {

        return ResponseEntity.ok(tecnicoService.actualizar(id, request));
    }

    // ================================
    // DELETE (LÓGICO)
    // ================================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        tecnicoService.eliminar(id);
    }

    // ================================
    // SUBIR IMAGEN
    // ================================
    @PutMapping("/{id}/imagen")
    public ResponseEntity<TecnicoResponse> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                tecnicoService.subirImagen(id, file)
        );
    }
}