package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.service.FixturePdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "13. Fixture", description = "Gestión del fixture semanal del torneo")
@RestController
@RequestMapping("/api/fixtures")
@RequiredArgsConstructor
public class FixturePdfController {

    private final FixturePdfService fixturePdfService;

    @Operation(
        summary = "Descargar PDF del fixture",
        description = """
            Genera y descarga el PDF del fixture para compartir por WhatsApp.
            
            Formato:
            - Encabezado con nombre del torneo, fecha y sede
            - Tabla con columnas: HORA | CAMPO | PARTIDO
            - Ordenado por hora y cancha
            - Pie de página con normas del torneo (Art. 31)
            
            El archivo se descarga como `fixture-fecha{N}.pdf`
            """
    )
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @Parameter(description = "ID del fixture") @PathVariable Long id) {

        byte[] pdf = fixturePdfService.generarPdf(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"fixture-" + id + ".pdf\"")
                .body(pdf);
    }
}
