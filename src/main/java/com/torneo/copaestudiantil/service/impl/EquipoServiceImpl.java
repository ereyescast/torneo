package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.dto.request.EquipoRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.request.search.EquipoSearchRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.EquipoService;
import com.torneo.copaestudiantil.specification.EquipoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final EdicionTorneoRepository edicionRepository;
    private final CategoriaRepository categoriaRepository;
    private final SedeRepository sedeRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorData<EquipoResponse> search(EquipoSearchRequest request) {
        if (request == null) request = new EquipoSearchRequest();
        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();

        int limit = pagination.getLimit();
        String sortBy = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        Sort.Direction dir = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Specification<Equipo> spec = EquipoSpecification.fromRequest(request);
        List<Equipo> results = equipoRepository.findAll(spec, Sort.by(dir, sortBy));
        List<Equipo> paginados = results.stream().limit(limit + 1L).toList();
        List<EquipoResponse> responses = paginados.stream().map(this::toResponse).toList();

        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public EquipoResponse crear(EquipoRequest request) {
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Equipo equipo = Equipo.builder()
                .organizadorId(request.getOrganizadorId())
                .edicion(edicion).categoria(categoria).sede(sede)
                .nombre(request.getNombre()).logoUrl(request.getLogoUrl())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return toResponse(equipoRepository.save(equipo));
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public EquipoResponse actualizar(Long id, EquipoRequest request) {
        Equipo equipo = findById(id);
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        equipo.setNombre(request.getNombre());
        equipo.setLogoUrl(request.getLogoUrl());
        equipo.setEdicion(edicion);
        equipo.setCategoria(categoria);
        equipo.setSede(sede);
        equipo.setActivo(request.getActivo() != null ? request.getActivo() : equipo.getActivo());

        return toResponse(equipoRepository.save(equipo));
    }

    @Override
    public void desactivar(Long id) {
        Equipo equipo = findById(id);
        equipo.setActivo(false);
        equipoRepository.save(equipo);
    }

    private Equipo findById(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + id));
    }

    private EquipoResponse toResponse(Equipo e) {
        return EquipoResponse.builder()
                .id(e.getId()).organizadorId(e.getOrganizadorId())
                .nombre(e.getNombre()).logoUrl(e.getLogoUrl()).activo(e.getActivo())
                .edicion(EdicionTorneoResponse.builder()
                        .id(e.getEdicion().getId()).nombre(e.getEdicion().getNombre())
                        .fechaInicio(e.getEdicion().getFechaInicio())
                        .fechaFin(e.getEdicion().getFechaFin())
                        .activa(e.getEdicion().getActiva()).build())
                .categoria(CategoriaResponse.builder()
                        .id(e.getCategoria().getId())
                        .anioNacimiento(e.getCategoria().getAnioNacimiento())
                        .modalidad(e.getCategoria().getModalidad())
                        .nivel(e.getCategoria().getNivel())
                        .activa(e.getCategoria().getActiva()).build())
                .sede(SedeResponse.builder()
                        .id(e.getSede().getId()).nombre(e.getSede().getNombre())
                        .direccion(e.getSede().getDireccion())
                        .activa(e.getSede().getActiva()).build())
                .build();
    }
}
