package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.dto.request.CategoriaRequest;
import com.torneo.copaestudiantil.dto.response.CategoriaResponse;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.CategoriaRepository;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import com.torneo.copaestudiantil.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final EdicionTorneoRepository edicionRepository;

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        Categoria categoria = Categoria.builder()
                .organizadorId(request.getOrganizadorId())
                .edicion(edicion)
                .anioNacimiento(request.getAnioNacimiento())
                .nivel(request.getNivel())
                .modalidad(request.getModalidad())
                .maxJugadoresPorEquipo(request.getMaxJugadoresPorEquipo())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .build();

        return toResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarPorEdicion(Long edicionId) {
        return categoriaRepository.findByEdicionId(edicionId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = findById(id);
        EdicionTorneo edicion = edicionRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

        categoria.setOrganizadorId(request.getOrganizadorId());
        categoria.setEdicion(edicion);
        categoria.setAnioNacimiento(request.getAnioNacimiento());
        categoria.setNivel(request.getNivel());
        categoria.setModalidad(request.getModalidad());
        categoria.setMaxJugadoresPorEquipo(request.getMaxJugadoresPorEquipo());

        return toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void desactivar(Long id) {
        Categoria categoria = findById(id);
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    private CategoriaResponse toResponse(Categoria c) {
        EdicionTorneoResponse edicionResponse = null;
        if (c.getEdicion() != null) {
            EdicionTorneo e = c.getEdicion();
            edicionResponse = EdicionTorneoResponse.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .fechaInicio(e.getFechaInicio())
                    .fechaFin(e.getFechaFin())
                    .activa(e.getActiva())
                    .build();
        }
        return CategoriaResponse.builder()
                .id(c.getId())
                .organizadorId(c.getOrganizadorId())
                .edicion(edicionResponse)
                .anioNacimiento(c.getAnioNacimiento())
                .nivel(c.getNivel())
                .modalidad(c.getModalidad())
                .maxJugadoresPorEquipo(c.getMaxJugadoresPorEquipo())
                .activa(c.getActiva())
                .build();
    }
}
