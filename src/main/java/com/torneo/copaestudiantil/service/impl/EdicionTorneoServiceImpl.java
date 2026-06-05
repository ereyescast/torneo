package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.dto.response.ExisteEdicionResponse;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import com.torneo.copaestudiantil.specification.EdicionTorneoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EdicionTorneoServiceImpl implements EdicionTorneoService {

    private final EdicionTorneoRepository edicionRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorData<EdicionTorneoResponse> search(EdicionSearchRequest request) {
        if (request == null) request = new EdicionSearchRequest();
        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();
        int limit = pagination.getLimit();
        String sortBy = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        Sort.Direction dir = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        // MULTI-TENANCY: forzar filtro por el organizador del token
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        Specification<EdicionTorneo> spec = EdicionTorneoSpecification.fromRequest(request)
                .and((root, query, cb) -> cb.equal(root.get("organizadorId"), organizadorId));

        List<EdicionTorneo> results = edicionRepository.findAll(spec, Sort.by(dir, sortBy));
        List<EdicionTorneo> paginados = results.stream().limit(limit + 1L).toList();
        List<EdicionTorneoResponse> responses = paginados.stream().map(this::toResponse).toList();
        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public EdicionTorneoResponse crear(EdicionTorneoRequest request) {
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();

        if (edicionRepository.existsByOrganizadorIdAndNombreAndFechaInicio(
                organizadorId, request.getNombre(), request.getFechaInicio())) {
            throw new BadRequestException(String.format(
                    "Ya existe una edición llamada '%s' que inicia el %s para este organizador. "
                            + "Usa la edición existente o cambia el nombre o la fecha de inicio.",
                    request.getNombre(), request.getFechaInicio()));
        }

        EdicionTorneo edicion = EdicionTorneo.builder()
                .organizadorId(organizadorId)
                .nombre(request.getNombre())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();
        return toResponse(edicionRepository.save(edicion));
    }

    @Override
    @Transactional(readOnly = true)
    public EdicionTorneoResponse obtenerPorId(Long id) {
        EdicionTorneo edicion = findById(id);
        SecurityUtils.validarPertenencia(edicion.getOrganizadorId());
        return toResponse(edicion);
    }

    @Override
    public EdicionTorneoResponse actualizar(Long id, EdicionTorneoRequest request) {
        EdicionTorneo edicion = findById(id);
        SecurityUtils.validarPertenencia(edicion.getOrganizadorId());
        Long organizadorId = edicion.getOrganizadorId();

        boolean cambioClave = !edicion.getNombre().equals(request.getNombre())
                || !edicion.getFechaInicio().equals(request.getFechaInicio());

        if (cambioClave && edicionRepository.existsByOrganizadorIdAndNombreAndFechaInicio(
                organizadorId, request.getNombre(), request.getFechaInicio())) {
            throw new BadRequestException(String.format(
                    "Ya existe otra edición '%s' que inicia el %s para este organizador.",
                    request.getNombre(), request.getFechaInicio()));
        }

        edicion.setNombre(request.getNombre());
        edicion.setFechaInicio(request.getFechaInicio());
        edicion.setFechaFin(request.getFechaFin());
        edicion.setActiva(request.getActiva() != null ? request.getActiva() : edicion.getActiva());
        return toResponse(edicionRepository.save(edicion));
    }

    @Override
    public void desactivar(Long id) {
        EdicionTorneo edicion = findById(id);
        SecurityUtils.validarPertenencia(edicion.getOrganizadorId());
        edicion.setActiva(false);
        edicionRepository.save(edicion);
    }

    @Override
    @Transactional(readOnly = true)
    public ExisteEdicionResponse verificarExistencia(Long organizadorIdIgnorado, String nombre,
                                                      LocalDate fechaInicio) {
        // El organizadorId del parámetro se ignora — se usa el del token por seguridad.
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        Optional<EdicionTorneo> existente = edicionRepository
                .findByOrganizadorIdAndNombreAndFechaInicio(organizadorId, nombre, fechaInicio);

        if (existente.isEmpty())
            return ExisteEdicionResponse.builder().existe(false).build();

        EdicionTorneo e = existente.get();
        return ExisteEdicionResponse.builder()
                .existe(true).id(e.getId()).nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                .fechaCreacion(e.getFechaCreacion()).build();
    }

    private EdicionTorneo findById(Long id) {
        return edicionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Edición no encontrada con id: " + id));
    }

    private EdicionTorneoResponse toResponse(EdicionTorneo e) {
        return EdicionTorneoResponse.builder()
                .id(e.getId()).organizadorId(e.getOrganizadorId()).nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                .activa(e.getActiva()).build();
    }
}
