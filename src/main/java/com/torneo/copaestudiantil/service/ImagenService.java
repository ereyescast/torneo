package com.torneo.copaestudiantil.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImagenService {

    String guardarImagenTecnico(Long tecnicoId, MultipartFile file);

    /**
     * Devuelve la imagen del técnico como Resource lista para servir.
     * Valida que el técnico exista, tenga imagen y que el archivo esté
     * en el servidor. Toda esa lógica vive aquí, no en el controller.
     */
    Resource obtenerImagenTecnico(Long tecnicoId);
}
