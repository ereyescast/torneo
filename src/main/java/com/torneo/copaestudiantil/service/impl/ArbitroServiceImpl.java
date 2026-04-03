package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.ArbitroRequest;
import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.entity.Arbitro;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.mapper.ArbitroMapper;
import com.torneo.copaestudiantil.repository.ArbitroRepository;
import com.torneo.copaestudiantil.service.ArbitroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArbitroServiceImpl implements ArbitroService {

    private final ArbitroRepository arbitroRepository;
    private final ArbitroMapper arbitroMapper;

    @Override
    public ArbitroResponse crear(Long organizadorId, ArbitroRequest request) {

        Arbitro arbitro = Arbitro.builder()
                .organizadorId(organizadorId)
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .activo(true)
                .build();

        return arbitroMapper.toResponse(arbitroRepository.save(arbitro));
    }

    @Override
    public List<ArbitroResponse> listarActivos(Long organizadorId) {

        return arbitroRepository
                .findByOrganizadorIdAndActivoTrue(organizadorId)
                .stream()
                .map(arbitroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArbitroResponse actualizar(Long organizadorId, Long arbitroId, ArbitroRequest request) {

        Arbitro arbitro = arbitroRepository
                .findByIdAndOrganizadorId(arbitroId, organizadorId)
                .orElseThrow(() -> new BadRequestException("Árbitro no encontrado"));

        arbitro.setNombre(request.getNombre());
        arbitro.setTelefono(request.getTelefono());
        arbitro.setEmail(request.getEmail());

        return arbitroMapper.toResponse(arbitroRepository.save(arbitro));
    }

    @Override
    public void desactivar(Long organizadorId, Long arbitroId) {

        Arbitro arbitro = arbitroRepository
                .findByIdAndOrganizadorId(arbitroId, organizadorId)
                .orElseThrow(() -> new BadRequestException("Árbitro no encontrado"));

        arbitro.setActivo(false);
        arbitroRepository.save(arbitro);
    }
}