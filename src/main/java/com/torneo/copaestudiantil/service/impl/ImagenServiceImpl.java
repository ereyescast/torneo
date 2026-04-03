package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.service.ImagenService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImagenServiceImpl implements ImagenService {

    @Override
    public String guardarImagenTecnico(Long tecnicoId, MultipartFile file) {

        if (file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new BadRequestException("Solo se permiten imágenes JPG o PNG");
        }

        try {
            String uploadDir = "uploads/tecnicos/";
            Files.createDirectories(Paths.get(uploadDir));

            String extension = contentType.equals("image/png") ? ".png" : ".jpg";
            String fileName = UUID.randomUUID() + extension;

            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            return "/" + uploadDir + fileName;

        } catch (IOException e) {
            throw new BadRequestException("Error al guardar la imagen");
        }
    }
}