package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.request.search.EdicionSearchRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import com.torneo.copaestudiantil.specification.EdicionTorneoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        Specification<EdicionTorneo> spec = EdicionTorneoSpecification.fromRequest(request);
        List<EdicionTorneo> results = edicionRepository.findAll(spec, Sort.by(dir, sortBy));
        List<EdicionTorneo> paginados = results.stream().limit(limit + 1L).toList();
        List<EdicionTorneoResponse> responses = paginados.stream().map(this::toResponse).toList();

        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public EdicionTorneoResponse crear(EdicionTorneoRequest request) {
        EdicionTorneo edicion = EdicionTorneo.builder()
                .organizadorId(request.getOrganizadorId())
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
        return toResponse(findById(id));
    }

    @Override
    public EdicionTorneoResponse actualizar(Long id, EdicionTorneoRequest request) {
        EdicionTorneo edicion = findById(id);
        edicion.setNombre(request.getNombre());
        edicion.setFechaInicio(request.getFechaInicio());
        edicion.setFechaFin(request.getFechaFin());
        edicion.setOrganizadorId(request.getOrganizadorId());
        edicion.setActiva(request.getActiva() != null ? request.getActiva() : edicion.getActiva());
        return toResponse(edicionRepository.save(edicion));
    }

    @Override
    public void desactivar(Long id) {
        EdicionTorneo edicion = findById(id);
        edicion.setActiva(false);
        edicionRepository.save(edicion);
    }

    private EdicionTorneo findById(Long id) {
        return edicionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada con id: " + id));
    }

    private EdicionTorneoResponse toResponse(EdicionTorneo e) {
        return EdicionTorneoResponse.builder()
                .id(e.getId()).nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                .activa(e.getActiva()).build();
    }
}
