package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.request.search.TecnicoSearchRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.TecnicoRepository;
import com.torneo.copaestudiantil.service.TecnicoService;
import com.torneo.copaestudiantil.specification.TecnicoSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public CursorData<TecnicoResponse> search(TecnicoSearchRequest request) {
        if (request == null) request = new TecnicoSearchRequest();
        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();
        int limit        = pagination.getLimit();
        String sortBy    = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        Sort.Direction dir = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        Specification<Tecnico> spec = TecnicoSpecification.fromRequest(request)
                .and((root, query, cb) -> cb.equal(root.get("organizadorId"), organizadorId));

        List<Tecnico> results = tecnicoRepository.findAll(spec, Sort.by(dir, sortBy));
        List<Tecnico> paginados = results.stream().limit(limit + 1L).toList();
        List<TecnicoResponse> responses = paginados.stream().map(this::toResponse).toList();
        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public TecnicoResponse registrar(TecnicoRequest request) {
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        if (tecnicoRepository.existsByNumeroDocumentoAndOrganizadorId(
                request.getNumeroDocumento(), organizadorId)) {
            throw new BadRequestException("El número de documento ya está registrado");
        }
        Tecnico tecnico = Tecnico.builder()
                .organizadorId(organizadorId)
                .nombres(request.getNombres())
                .apellidosPaterno(request.getApellidosPaterno())
                .apellidosMaterno(request.getApellidosMaterno())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nacionalidad(request.getNacionalidad())
                .fechaNac(request.getFechaNac())
                .activo(true)
                .build();
        return toResponse(tecnicoRepository.save(tecnico));
    }

    @Override
    public TecnicoResponse obtenerPorId(Long id) {
        Tecnico tecnico = findById(id);
        SecurityUtils.validarPertenencia(tecnico.getOrganizadorId());
        return toResponse(tecnico);
    }

    @Override
    public TecnicoResponse actualizar(Long id, TecnicoRequest request) {
        Tecnico tecnico = findById(id);
        SecurityUtils.validarPertenencia(tecnico.getOrganizadorId());
        Long organizadorId = tecnico.getOrganizadorId();

        if (!tecnico.getNumeroDocumento().equals(request.getNumeroDocumento())
                && tecnicoRepository.existsByNumeroDocumentoAndOrganizadorId(
                        request.getNumeroDocumento(), organizadorId)) {
            throw new BadRequestException("El número de documento ya está registrado");
        }
        tecnico.setNombres(request.getNombres());
        tecnico.setApellidosPaterno(request.getApellidosPaterno());
        tecnico.setApellidosMaterno(request.getApellidosMaterno());
        tecnico.setTipoDocumento(request.getTipoDocumento());
        tecnico.setNumeroDocumento(request.getNumeroDocumento());
        tecnico.setNacionalidad(request.getNacionalidad());
        tecnico.setFechaNac(request.getFechaNac());
        return toResponse(tecnicoRepository.save(tecnico));
    }

    @Override
    public void eliminar(Long id) {
        Tecnico tecnico = findById(id);
        SecurityUtils.validarPertenencia(tecnico.getOrganizadorId());
        tecnico.setActivo(false);
        tecnicoRepository.save(tecnico);
    }

    @Override
    public TecnicoResponse subirImagen(Long id, MultipartFile file) {
        Tecnico tecnico = findById(id);
        SecurityUtils.validarPertenencia(tecnico.getOrganizadorId());

        if (file.isEmpty())
            throw new BadRequestException("El archivo está vacío");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new BadRequestException("La imagen no debe superar los 5MB");
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png")))
            throw new BadRequestException("Formato no soportado. Use JPG o PNG.");
        try {
            String dir = uploadDir + "/tecnicos/";
            Files.createDirectories(Paths.get(dir));
            String ext = contentType.equalsIgnoreCase("image/png") ? ".png" : ".jpg";
            String fileName = "tecnico_" + id + "_" + System.currentTimeMillis() + ext;
            Path filePath = Paths.get(dir + fileName);
            Files.write(filePath, file.getBytes());
            tecnico.setProfileImage("/" + dir + fileName);
            return toResponse(tecnicoRepository.save(tecnico));
        } catch (IOException e) {
            throw new BadRequestException("Error al guardar la imagen");
        }
    }

    private Tecnico findById(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));
    }

    private TecnicoResponse toResponse(Tecnico t) {
        return TecnicoResponse.builder()
                .id(t.getId())
                .nombres(t.getNombres())
                .apellidosPaterno(t.getApellidosPaterno())
                .apellidosMaterno(t.getApellidosMaterno())
                .tipoDocumento(t.getTipoDocumento())
                .numeroDocumento(t.getNumeroDocumento())
                .nacionalidad(t.getNacionalidad())
                .fechaNac(t.getFechaNac())
                .profileImage(t.getProfileImage())
                .activo(t.getActivo())
                .build();
    }
}
