package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.mapper.TecnicoMapper;
import com.torneo.copaestudiantil.repository.TecnicoRepository;
import com.torneo.copaestudiantil.service.TecnicoService;
import com.torneo.copaestudiantil.specification.TecnicoSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final TecnicoMapper tecnicoMapper;

    // ================================
    // CREATE
    // ================================
    @Override
    public TecnicoResponse registrar(TecnicoRequest request) {

        if (tecnicoRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new BadRequestException("El número de documento ya está registrado");
        }

        Tecnico tecnico = tecnicoMapper.toEntity(request);
        tecnico.setActivo(true);

        tecnicoRepository.save(tecnico);

        return tecnicoMapper.toResponse(tecnico);
    }

    // ================================
    // READ BY ID
    // ================================
    @Override
    public TecnicoResponse obtenerPorId(Long id) {

        Tecnico tecnico = tecnicoRepository.findById(id)
                .filter(Tecnico::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        return tecnicoMapper.toResponse(tecnico);
    }

    // ================================
    // SEARCH + PAGINATION
    // ================================
    @Override
    public Page<TecnicoResponse> buscar(
            String nombres,
            String numeroDocumento,
            String nacionalidad,
            Pageable pageable) {

        Specification<Tecnico> spec = Specification
                .where(TecnicoSpecification.activo())
                .and(TecnicoSpecification.nombresLike(nombres))
                .and(TecnicoSpecification.documentoEquals(numeroDocumento))
                .and(TecnicoSpecification.nacionalidadLike(nacionalidad));

        return tecnicoRepository
                .findAll(spec, pageable)
                .map(tecnicoMapper::toResponse);
    }

    // ================================
    // UPDATE
    // ================================
    @Override
    public TecnicoResponse actualizar(Long id, TecnicoRequest request) {

        Tecnico tecnico = tecnicoRepository.findById(id)
                .filter(Tecnico::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        if (!tecnico.getNumeroDocumento().equals(request.getNumeroDocumento())
                && tecnicoRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {

            throw new BadRequestException("El número de documento ya está registrado");
        }

        tecnico.setNombres(request.getNombres());
        tecnico.setApellidosPaterno(request.getApellidosPaterno());
        tecnico.setApellidosMaterno(request.getApellidosMaterno());
        tecnico.setTipoDocumento(request.getTipoDocumento());
        tecnico.setNumeroDocumento(request.getNumeroDocumento());
        tecnico.setNacionalidad(request.getNacionalidad());
        tecnico.setFechaNac(request.getFechaNac());

        return tecnicoMapper.toResponse(tecnico);
    }

    // ================================
    // DELETE (LÓGICO)
    // ================================
    @Override
    public void eliminar(Long id) {

        Tecnico tecnico = tecnicoRepository.findById(id)
                .filter(Tecnico::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        tecnico.setActivo(false);
    }

    // ================================
    // UPLOAD IMAGE
    // ================================
    @Override
    public TecnicoResponse subirImagen(Long id, MultipartFile file) {

        Tecnico tecnico = tecnicoRepository.findById(id)
                .filter(Tecnico::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        if (file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("La imagen no debe superar los 5MB");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png"))) {

            throw new BadRequestException("Formato no soportado. Use JPG o PNG.");
        }

        try {
            String uploadDir = "uploads/tecnicos/";
            Files.createDirectories(Paths.get(uploadDir));

            String extension = contentType.equalsIgnoreCase("image/png") ? ".png" : ".jpg";
            String fileName = "tecnico_" + id + extension;

            Path filePath = Paths.get(uploadDir + fileName);

            Files.write(filePath, file.getBytes());

            tecnico.setProfileImage("/" + uploadDir + fileName);

            return tecnicoMapper.toResponse(tecnico);

        } catch (IOException e) {
            throw new BadRequestException("Error al guardar la imagen");
        }
    }
}