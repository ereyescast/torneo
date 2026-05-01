package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.SedeRequest;
import com.torneo.copaestudiantil.dto.response.SedeResponse;
import com.torneo.copaestudiantil.entity.Sede;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.SedeRepository;
import com.torneo.copaestudiantil.service.SedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;

    @Override
    public SedeResponse crear(SedeRequest request) {
        Sede sede = Sede.builder()
                .organizadorId(request.getOrganizadorId())
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();
        return toResponse(sedeRepository.save(sede));
    }

    @Override
    @Transactional(readOnly = true)
    public SedeResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeResponse> listarTodas() {
        return sedeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeResponse> listarPorOrganizador(Long organizadorId) {
        return sedeRepository.findByOrganizadorId(organizadorId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public SedeResponse actualizar(Long id, SedeRequest request) {
        Sede sede = findById(id);
        sede.setNombre(request.getNombre());
        sede.setDireccion(request.getDireccion());
        sede.setOrganizadorId(request.getOrganizadorId());
        return toResponse(sedeRepository.save(sede));
    }

    @Override
    public void desactivar(Long id) {
        Sede sede = findById(id);
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
