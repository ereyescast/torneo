package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.request.search.ArbitroSearchRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.entity.Arbitro;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.ArbitroRepository;
import com.torneo.copaestudiantil.service.ArbitroService;
import com.torneo.copaestudiantil.specification.ArbitroSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArbitroServiceImpl implements ArbitroService {

    private final ArbitroRepository arbitroRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorData<ArbitroResponse> search(ArbitroSearchRequest request) {
        if (request == null) request = new ArbitroSearchRequest();
        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();

        int limit = pagination.getLimit();
        String sortBy = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        Sort.Direction dir = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Specification<Arbitro> spec = ArbitroSpecification.fromRequest(request);
        List<Arbitro> results = arbitroRepository.findAll(spec, Sort.by(dir, sortBy));
        List<Arbitro> paginados = results.stream().limit(limit + 1L).toList();
        List<ArbitroResponse> responses = paginados.stream().map(this::toResponse).toList();

        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public ArbitroResponse crear(Long organizadorId, ArbitroRequest request) {
        Arbitro arbitro = Arbitro.builder()
                .organizadorId(organizadorId)
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .activo(true)
                .build();
        return toResponse(arbitroRepository.save(arbitro));
    }

    @Override
    @Transactional(readOnly = true)
    public ArbitroResponse obtenerPorId(Long id) {
        return toResponse(arbitroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Árbitro no encontrado")));
    }

    @Override
    public ArbitroResponse actualizar(Long organizadorId, Long arbitroId, ArbitroRequest request) {
        Arbitro arbitro = arbitroRepository.findByIdAndOrganizadorId(arbitroId, organizadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Árbitro no encontrado"));
        arbitro.setNombre(request.getNombre());
        arbitro.setTelefono(request.getTelefono());
        arbitro.setEmail(request.getEmail());
        return toResponse(arbitroRepository.save(arbitro));
    }

    @Override
    public void desactivar(Long organizadorId, Long arbitroId) {
        Arbitro arbitro = arbitroRepository.findByIdAndOrganizadorId(arbitroId, organizadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Árbitro no encontrado"));
        arbitro.setActivo(false);
        arbitroRepository.save(arbitro);
    }

    private ArbitroResponse toResponse(Arbitro a) {
        return ArbitroResponse.builder()
                .id(a.getId()).nombre(a.getNombre())
                .telefono(a.getTelefono()).email(a.getEmail())
                .activo(a.getActivo()).build();
    }
}
