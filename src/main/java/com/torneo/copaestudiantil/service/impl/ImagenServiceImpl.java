package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.TecnicoRepository;
import com.torneo.copaestudiantil.service.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagenServiceImpl implements ImagenService {

    private final TecnicoRepository tecnicoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

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
            String dir = "uploads/tecnicos/";
            Files.createDirectories(Paths.get(dir));

            String extension = contentType.equals("image/png") ? ".png" : ".jpg";
            String fileName = UUID.randomUUID() + extension;

            Path filePath = Paths.get(dir + fileName);
            Files.write(filePath, file.getBytes());

            return "/" + dir + fileName;

        } catch (IOException e) {
            throw new BadRequestException("Error al guardar la imagen");
        }
    }

    @Override
    public Resource obtenerImagenTecnico(Long tecnicoId) {

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        if (tecnico.getProfileImage() == null)
            throw new ResourceNotFoundException("El técnico no tiene imagen");

        try {
            Path path = Paths.get(uploadDir)
                    .resolve(tecnico.getProfileImage()).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists())
                throw new ResourceNotFoundException("Imagen no encontrada en el servidor");

            return resource;

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error al cargar la imagen");
        }
    }
}
