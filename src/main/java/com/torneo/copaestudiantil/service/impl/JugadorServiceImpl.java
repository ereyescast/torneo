package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.request.search.JugadorSearchRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import com.torneo.copaestudiantil.entity.Jugador;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.JugadorRepository;
import com.torneo.copaestudiantil.service.JugadorService;
import com.torneo.copaestudiantil.specification.JugadorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JugadorServiceImpl implements JugadorService {

    private final JugadorRepository jugadorRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ── Search con cursor enriquecido ────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CursorData<JugadorResponse> search(JugadorSearchRequest request) {
        if (request == null) request = new JugadorSearchRequest();

        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();

        int limit        = pagination.getLimit();
        String sortBy    = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        String direction = pagination.getDirection();
        Sort.Direction dir = "DESC".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Construir Specification con todos los filtros
        Specification<Jugador> spec = JugadorSpecification.fromRequest(request);

        // Traer limit+1 para detectar si hay siguiente página
        List<Jugador> results = jugadorRepository.findAll(spec, Sort.by(dir, sortBy));
        List<Jugador> paginados = results.stream().limit(limit + 1L).toList();

        // Mapear a response
        List<JugadorResponse> responses = paginados.stream()
                .map(this::toResponse).toList();

        // Construir CursorData con cursor enriquecido
        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @Override
    public JugadorResponse crear(JugadorRequest request) {
        if (jugadorRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new BadRequestException("Ya existe un jugador con el documento: "
                    + request.getNumeroDocumento());
        }
        Jugador jugador = Jugador.builder()
                .nombres(request.getNombres())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .fechaNacimiento(request.getFechaNacimiento())
                .nacionalidad(request.getNacionalidad())
                .activo(true)
                .build();
        return toResponse(jugadorRepository.save(jugador));
    }

    @Override
    @Transactional(readOnly = true)
    public JugadorResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public JugadorResponse obtenerPorDocumento(String numeroDocumento) {
        return jugadorRepository.findByNumeroDocumento(numeroDocumento)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Jugador no encontrado con documento: " + numeroDocumento));
    }

    @Override
    public JugadorResponse actualizar(Long id, JugadorRequest request) {
        Jugador jugador = findById(id);
        if (!jugador.getNumeroDocumento().equals(request.getNumeroDocumento())
                && jugadorRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new BadRequestException("Ya existe un jugador con el documento: "
                    + request.getNumeroDocumento());
        }
        jugador.setNombres(request.getNombres());
        jugador.setApellidoPaterno(request.getApellidoPaterno());
        jugador.setApellidoMaterno(request.getApellidoMaterno());
        jugador.setTipoDocumento(request.getTipoDocumento());
        jugador.setNumeroDocumento(request.getNumeroDocumento());
        jugador.setFechaNacimiento(request.getFechaNacimiento());
        jugador.setNacionalidad(request.getNacionalidad());
        jugador.setActivo(request.getActivo() != null
                ? request.getActivo() : jugador.getActivo());
        return toResponse(jugadorRepository.save(jugador));
    }

    @Override
    public void desactivar(Long id) {
        Jugador jugador = findById(id);
        jugador.setActivo(false);
        jugadorRepository.save(jugador);
    }

    @Override
    public JugadorResponse subirImagen(Long id, MultipartFile file) {
        Jugador jugador = findById(id);
        if (!Boolean.TRUE.equals(jugador.getActivo()))
            throw new BadRequestException("No se puede subir imagen a un jugador inactivo");
        if (file.isEmpty())
            throw new BadRequestException("El archivo está vacío");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new BadRequestException("La imagen no debe superar los 5MB");

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png")))
            throw new BadRequestException("Solo se permiten imágenes JPG o PNG");

        try {
            String dir = uploadDir + "/jugadores/";
            Files.createDirectories(Paths.get(dir));
            String ext = contentType.equalsIgnoreCase("image/png") ? ".png" : ".jpg";
            String fileName = "jugador_" + id + "_" + UUID.randomUUID() + ext;
            Path path = Paths.get(dir + fileName);
            Files.write(path, file.getBytes());
            jugador.setProfileImage("/" + dir + fileName);
            return toResponse(jugadorRepository.save(jugador));
        } catch (IOException e) {
            throw new BadRequestException("Error al guardar la imagen");
        }
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private Jugador findById(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Jugador no encontrado con id: " + id));
    }

    private JugadorResponse toResponse(Jugador j) {
        return JugadorResponse.builder()
                .id(j.getId())
                .nombres(j.getNombres())
                .apellidoPaterno(j.getApellidoPaterno())
                .apellidoMaterno(j.getApellidoMaterno())
                .tipoDocumento(j.getTipoDocumento())
                .numeroDocumento(j.getNumeroDocumento())
                .fechaNacimiento(j.getFechaNacimiento())
                .nacionalidad(j.getNacionalidad())
                .profileImage(j.getProfileImage())
                .activo(j.getActivo())
                .build();
    }
}
