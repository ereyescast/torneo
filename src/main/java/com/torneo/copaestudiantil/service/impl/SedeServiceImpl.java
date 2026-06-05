package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.request.search.SedeSearchRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;
import com.torneo.copaestudiantil.entity.Sede;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.SedeRepository;
import com.torneo.copaestudiantil.service.SedeService;
import com.torneo.copaestudiantil.specification.SedeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorData<SedeResponse> search(SedeSearchRequest request) {
        if (request == null) request = new SedeSearchRequest();
        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();
        int limit = pagination.getLimit();
        String sortBy = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        Sort.Direction dir = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        // MULTI-TENANCY: forzar el filtro por el organizador del token.
        // El usuario NO puede buscar sedes de otro organizador.
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();

        Specification<Sede> spec = SedeSpecification.fromRequest(request)
                .and((root, query, cb) -> cb.equal(root.get("organizadorId"), organizadorId));

        List<Sede> results = sedeRepository.findAll(spec, Sort.by(dir, sortBy));
        List<Sede> paginados = results.stream().limit(limit + 1L).toList();
        List<SedeResponse> responses = paginados.stream().map(this::toResponse).toList();
        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public SedeResponse crear(SedeRequest request) {
        // MULTI-TENANCY: el organizadorId sale del token, no del body.
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();

        Sede sede = Sede.builder()
                .organizadorId(organizadorId)
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();
        return toResponse(sedeRepository.save(sede));
    }

    @Override
    @Transactional(readOnly = true)
    public SedeResponse obtenerPorId(Long id) {
        Sede sede = findById(id);
        // MULTI-TENANCY: validar que la sede pertenezca al organizador del token.
        SecurityUtils.validarPertenencia(sede.getOrganizadorId());
        return toResponse(sede);
    }

    @Override
    public SedeResponse actualizar(Long id, SedeRequest request) {
        Sede sede = findById(id);
        // MULTI-TENANCY: no puedes actualizar una sede de otro organizador.
        SecurityUtils.validarPertenencia(sede.getOrganizadorId());

        sede.setNombre(request.getNombre());
        sede.setDireccion(request.getDireccion());
        sede.setActiva(request.getActiva() != null ? request.getActiva() : sede.getActiva());
        // El organizadorId NO se cambia — la sede sigue siendo del mismo dueño.
        return toResponse(sedeRepository.save(sede));
    }

    @Override
    public void desactivar(Long id) {
        Sede sede = findById(id);
        // MULTI-TENANCY: no puedes desactivar una sede de otro organizador.
        SecurityUtils.validarPertenencia(sede.getOrganizadorId());
        sede.setActiva(false);
        sedeRepository.save(sede);
    }

    private Sede findById(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con id: " + id));
    }

    private SedeResponse toResponse(Sede s) {
        return SedeResponse.builder()
                .id(s.getId())
                .organizadorId(s.getOrganizadorId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .activa(s.getActiva())
                .build();
    }
}
