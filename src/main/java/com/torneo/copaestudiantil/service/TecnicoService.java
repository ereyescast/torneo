package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TecnicoService {

    // CREATE
    TecnicoResponse registrar(TecnicoRequest request);

    // READ - BY ID
    TecnicoResponse obtenerPorId(Long id);

    // READ - SEARCH WITH FILTERS + PAGINATION
    Page<TecnicoResponse> buscar(
            String nombres,
            String numeroDocumento,
            String nacionalidad,
            Pageable pageable
    );

    // UPDATE
    TecnicoResponse actualizar(Long id, TecnicoRequest request);

    // DELETE (lógico)
    void eliminar(Long id);

    // UPLOAD IMAGE
    TecnicoResponse subirImagen(Long id, MultipartFile file);
}