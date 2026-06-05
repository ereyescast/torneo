package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.common.response.CursorUtil;
import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.request.CategoriaRequest;
import com.torneo.copaestudiantil.dto.request.search.CategoriaSearchRequest;
import com.torneo.copaestudiantil.dto.request.search.CursorRequest;
import com.torneo.copaestudiantil.dto.response.CategoriaResponse;
import com.torneo.copaestudiantil.dto.response.EdicionTorneoResponse;
import com.torneo.copaestudiantil.entity.Categoria;
import com.torneo.copaestudiantil.entity.EdicionTorneo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.CategoriaRepository;
import com.torneo.copaestudiantil.repository.EdicionTorneoRepository;
import com.torneo.copaestudiantil.service.CategoriaService;
import com.torneo.copaestudiantil.specification.CategoriaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    @Transactional(readOnly = true)
    public CursorData<CategoriaResponse> search(CategoriaSearchRequest request) {
        if (request == null) request = new CategoriaSearchRequest();
        CursorRequest pagination = request.getPagination() != null
                ? request.getPagination() : new CursorRequest();
        int limit = pagination.getLimit();
        String sortBy = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
        Sort.Direction dir = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        Specification<Categoria> spec = CategoriaSpecification.fromRequest(request)
                .and((root, query, cb) -> cb.equal(root.get("organizadorId"), organizadorId));

        List<Categoria> results = categoriaRepository.findAll(spec, Sort.by(dir, sortBy));
        List<Categoria> paginados = results.stream().limit(limit + 1L).toList();
        List<CategoriaResponse> responses = paginados.stream().map(this::toResponse).toList();
        return CursorUtil.build(responses, limit, sortBy, pagination.getPreviousCursor());
    }

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        Long organizadorId = SecurityUtils.getOrganizadorIdActual();
        EdicionTorneo edicion = cargarEdicionDelOrganizador(request.getEdicionId(), organizadorId);

        Categoria categoria = Categoria.builder()
                .organizadorId(organizadorId)
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
        Categoria categoria = findById(id);
        SecurityUtils.validarPertenencia(categoria.getOrganizadorId());
        return toResponse(categoria);
    }

    @Override
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = findById(id);
        SecurityUtils.validarPertenencia(categoria.getOrganizadorId());
        Long organizadorId = categoria.getOrganizadorId();

        EdicionTorneo edicion = cargarEdicionDelOrganizador(request.getEdicionId(), organizadorId);

        categoria.setEdicion(edicion);
        categoria.setAnioNacimiento(request.getAnioNacimiento());
        categoria.setNivel(request.getNivel());
        categoria.setModalidad(request.getModalidad());
        categoria.setMaxJugadoresPorEquipo(request.getMaxJugadoresPorEquipo());
        categoria.setActiva(request.getActiva() != null ? request.getActiva() : categoria.getActiva());
        return toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void desactivar(Long id) {
        Categoria categoria = findById(id);
        SecurityUtils.validarPertenencia(categoria.getOrganizadorId());
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    /** Carga una edición y valida que pertenezca al organizador (evita usar edición ajena). */
    private EdicionTorneo cargarEdicionDelOrganizador(Long edicionId, Long organizadorId) {
        EdicionTorneo edicion = edicionRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        SecurityUtils.validarPertenencia(edicion.getOrganizadorId());
        return edicion;
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
                    .id(e.getId()).organizadorId(e.getOrganizadorId()).nombre(e.getNombre())
                    .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                    .activa(e.getActiva()).build();
        }
        return CategoriaResponse.builder()
                .id(c.getId()).organizadorId(c.getOrganizadorId())
                .edicion(edicionResponse).anioNacimiento(c.getAnioNacimiento())
                .nivel(c.getNivel()).modalidad(c.getModalidad())
                .maxJugadoresPorEquipo(c.getMaxJugadoresPorEquipo())
                .activa(c.getActiva()).build();
    }
}
