package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.EdicionTorneoRequest;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import com.torneo.copaestudiantil.service.EdicionTorneoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EdicionTorneoServiceImpl implements EdicionTorneoService {

    private final EdicionTorneoRepository edicionRepository;

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
    @Transactional(readOnly = true)
    public List<EdicionTorneoResponse> listarTodas() {
        return edicionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EdicionTorneoResponse> listarPorOrganizador(Long organizadorId) {
        return edicionRepository.findByOrganizadorId(organizadorId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public EdicionTorneoResponse actualizar(Long id, EdicionTorneoRequest request) {
        EdicionTorneo edicion = findById(id);
        edicion.setNombre(request.getNombre());
        edicion.setFechaInicio(request.getFechaInicio());
        edicion.setFechaFin(request.getFechaFin());
        edicion.setOrganizadorId(request.getOrganizadorId());
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
                .id(e.getId())
                .nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .activa(e.getActiva())
                .build();
    }
}
